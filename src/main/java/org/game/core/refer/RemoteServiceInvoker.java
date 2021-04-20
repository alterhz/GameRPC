package org.game.core.refer;

import org.apache.commons.lang3.StringUtils;
import org.game.core.*;
import org.game.core.exchange.Request;
import org.game.global.ServiceConsts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * 动态代理
 * <p>创建动态代理的实现类</p>
 * @author Ziegler
 * date 2021/4/13
 */
public class RemoteServiceInvoker<T> implements InvocationHandler {

    private final Class<T> type;

    /** logger */
    private static final Logger logger = LoggerFactory.getLogger(RemoteServiceInvoker.class);

    public RemoteServiceInvoker(Class<T> type) {
        this.type = type;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException, IOException, ExecutionException, InterruptedException {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 0) {
            if ("toString".equals(methodName)) {
                return this.toString();
            } else if ("$destroy".equals(methodName)) {
                return null;
            } else if ("hashCode".equals(methodName)) {
                return this.hashCode();
            }
        } else if (parameterTypes.length == 1 && "equals".equals(methodName)) {
            return this.equals(args[0]);
        }

        if (logger.isTraceEnabled()) {
            logger.debug("rpc invoke type = " + type.getName()
                    + ", methodName = " + methodName
                    + ", args = " + StringUtils.join(args));
        }

        if (ServicePort.getServicePort() == null) {
            throw new IllegalStateException("RPC调用需要在ServicePort线程。");
        }

        // 服务的远程调用点
        final CallPoint callPoint = getServiceCallPoint();
        // 当前线程的调用点
        final FromPoint fromPoint = ServicePort.getServicePort().getFromPoint();

        // rpc调用
        final Request request = new Request(Request.allocId());

        final RpcInvocation rpcInvocation = new RpcInvocation(fromPoint, callPoint, method, args);
        request.setRpcInvocation(rpcInvocation);

        // 当前node节点，无需网络，直接分发
        if (rpcInvocation.isOneWay()) {
            // 分发Request
            sendRequest(request, callPoint.getNode());
            return null;
        } else {
            // 等待rpc返回
            DefaultFuture future = DefaultFuture.newFuture(request, 30 * 1000);
            // 分发Request
            sendRequest(request, callPoint.getNode());
            if (rpcInvocation.isCompletableFuture()) {
                return future;
            }

            final long t1 = System.currentTimeMillis();
            while (!future.isDone()) {
                // 判断当前是ServicePort线程
                ServicePort.getServicePort().pulseOne();
                if (System.currentTimeMillis() - t1 > 10 * 1000) {
                    return new TimeoutException("RPC阻塞调用超时。");
                }
            }

            return future.get();
        }
    }

    private void sendRequest(Request request, String callNodeName) {
        final ServiceNode curNode = ServicePort.getServicePort().getServiceNode();
        if (!ServiceConsts.RPC_ALWAYS_USE_TRANSPORT && callNodeName.equals(curNode.getName())) {
            // 不走网络，直接派发数据
            curNode.dispatchRequest(request);
        } else {
            curNode.getNode(callNodeName).send(request);
        }
    }

    /**
     * 通过Service接口类的 {@link ServiceConfig} 的注解获取 {@link CallPoint}
     * @return 返回调用点
     */
    private CallPoint getServiceCallPoint() {
        final ServiceConfig serviceConfig = type.getAnnotation(ServiceConfig.class);
        final String serviceName = type.getName();
        return new CallPoint(serviceConfig.node(), serviceConfig.port(), serviceName);
    }

}
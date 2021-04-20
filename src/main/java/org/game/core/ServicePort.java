package org.game.core;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.game.core.exchange.Request;
import org.game.core.exchange.Response;
import org.game.global.ServiceConsts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 服务线程节点
 * <p>服务线程节点，对应线程，每个线程对应一个ServicePort。一个ServicePort下面可以挂载或管理若干个Service</p>
 * @author Ziegler
 * date 2021/4/12
 */
public class ServicePort implements Runnable {

    /** 所属node */
    private static final ThreadLocal<ServicePort> THREAD_LOCAL_SERVICE_PORT = new ThreadLocal<>();
    /** 一帧的时间长度 */
    public static final long ONE_FRAME_MILLIS = 20L;

    private final String name;
    /** 服务列表 {@literal name -> Service} */
    private final Map<String, Service> services = new ConcurrentHashMap<>();
    private final ServiceNode parentNode;
    /** rpc发送地址 */
    private final FromPoint fromPoint;

    /** 收到的{@link Request}线程安全队列  */
    private final ConcurrentLinkedQueue<Request> receivedRequests = new ConcurrentLinkedQueue<>();

    /** 收到的{@link Response}线程安全队列 */
    private final ConcurrentLinkedQueue<Response> receivedResponses = new ConcurrentLinkedQueue<>();

    /** future的超时处理 */
    private final Map<Long, DefaultFuture> allFutureTimeout = new ConcurrentHashMap<>();

    /** 下一帧的时间，用于控制每秒的帧频 */
    private long nextFrameTick;

    /** logger */
    private static final Logger logger = LoggerFactory.getLogger(ServicePort.class);

    public ServicePort(String name, ServiceNode parentNode) {
        this.name = name;
        this.parentNode = parentNode;
        this.fromPoint = new FromPoint(parentNode.getName(), getName());
    }

    /**
     * 添加{@link DefaultFuture}超时处理
     */
    public void addFutureTimeoutTask(DefaultFuture future) {
        allFutureTimeout.put(future.getId(), future);
        logger.debug("add future. future = {}, threadId = {}", future, Thread.currentThread().getId());
    }

    public void addRequest(Request request) {
        receivedRequests.add(request);
    }

    public void addResponse(Response response) {
        receivedResponses.add(response);
    }

    /** 线程所属node */
    public static ServicePort getServicePort() {
        return THREAD_LOCAL_SERVICE_PORT.get();
    }

    /**
     * RPC调用地址
     */
    public FromPoint getFromPoint() {
        return new FromPoint(parentNode.getName(), getName());
    }

    public void addService(String name, Service service) {
        services.put(name, service);
        logger.info("Service added. name = {}", name);
    }

    public ServiceNode getServiceNode() {
        return parentNode;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", name)
                .toString();
    }

    @Override
    public void run() {
        THREAD_LOCAL_SERVICE_PORT.set(this);

        init();

        pulse();

        THREAD_LOCAL_SERVICE_PORT.set(null);
    }

    private void init() {
        final Iterator<Service> iterator = services.values().iterator();
        while (iterator.hasNext()) {
            final Service service = iterator.next();
            service.init();
        }
    }

    private void pulse() {
        while (!Thread.currentThread().isInterrupted()) {
            while (true) {
                if (!pulseOne()) {
                    try {
                        Thread.sleep(1L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 单次执行心跳
     * @return 只要执行一个 {@link Request} 或者 {@link Response} 就返回 {@code true}
     */
    public boolean pulseOne() {
        final boolean execRequest = pollRequest();
        final boolean execResponse = pollResponse();
        // 每帧逻辑
        final long now = System.currentTimeMillis();
        if (now > nextFrameTick) {
            nextFrameTick = now + ONE_FRAME_MILLIS;

            final long t1 = System.currentTimeMillis();
            servicePulse(now);
            final long interval = System.currentTimeMillis() - t1;
            if (interval > 100L) {
                logger.warn("servicePulse > 100 Millis. interval = {}", interval);
            }

            futureTimeout();
        }
        return execRequest || execResponse;
    }

    private void servicePulse(long now) {
        final Iterator<Service> iterator = services.values().iterator();
        while (iterator.hasNext()) {
            final Service service = iterator.next();
            service.pulse(now);
        }
    }

    private void futureTimeout() {
        allFutureTimeout.entrySet().removeIf(entry -> {
            final DefaultFuture defaultFuture = entry.getValue();
            final boolean timeout = defaultFuture.isTimeout();
            if (timeout) {
                defaultFuture.cancel(true);
                logger.warn("DefaultFuture超时。defaultFuture = {}", defaultFuture.toString());
            }
            return timeout;
        });
    }

    private boolean pollResponse() {
        final Response response = receivedResponses.poll();
        if (response == null) {
            return false;
        }
        logger.debug("execute response = {}", response);
        final DefaultFuture future = DefaultFuture.getFuture(response.getId());
        if (response.getStatus() == 0) {
            future.complete(response.getResult());
        } else {
            future.completeExceptionally(new RuntimeException("出现错误!"));
        }
        final DefaultFuture removeFuture = allFutureTimeout.remove(response.getId());
        logger.debug("remove future. future = {}, threadId = {}", removeFuture, Thread.currentThread().getId());
        return true;
    }

    private boolean pollRequest() {
        final Request request = receivedRequests.poll();
        if (request == null) {
            return false;
        }
        RpcInvocation rpcInvocation = request.getRpcInvocation();
        final String serviceName = rpcInvocation.getCallPoint().getService();
        final String methodName = rpcInvocation.getMethodName();
        final Service service = services.get(serviceName);
        if (service == null) {
            logger.warn("service == null.serviceName = {}", serviceName);
        } else {
            try {
                final Object result = MethodUtils.invokeExactMethod(service, methodName, rpcInvocation.getMethodArgs());
                if (result == null) {
                    logger.debug("RPC return type : void");
                } else {
                    if (result instanceof CompletableFuture) {
                        // 返回应答消息
                        final CompletableFuture<?> completableFuture = (CompletableFuture<?>) result;
                        completableFuture.whenComplete((o, throwable) -> {
                            final Response response = new Response(request.getId(), 0);
                            response.setResult(o);

                            final String replyNodeName = request.getRpcInvocation().getFromPoint().getNode();
                            sendResponse(response, replyNodeName);
                        });
                    } else {
                        // Integer,Long,String等数据类型直接返回
                        final Response response = new Response(request.getId(), 0);
                        response.setResult(result);

                        final String replyNodeName = request.getRpcInvocation().getFromPoint().getNode();
                        sendResponse(response, replyNodeName);
                    }
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private void sendResponse(Response response, String replyNodeName) {
        final ServiceNode curNode = ServicePort.getServicePort().getServiceNode();
        if (!ServiceConsts.RPC_ALWAYS_USE_TRANSPORT && replyNodeName.equals(curNode.getName())) {
            // 当前node，直接转发
            // 返回应答消息
            curNode.dispatchResponse(response);
        } else {
            // TODO code 网络发送rpc应答
            curNode.getNode(replyNodeName).send(response);
        }
    }
}

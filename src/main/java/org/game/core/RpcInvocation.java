package org.game.core;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

/**
 * rpc调用信息
 * <p>包括目标调用点信息、发起调用的信息、调用方法和参数等</p>
 *
 * @author Ziegler
 * date 2021/4/12
 */
public final class RpcInvocation implements Serializable {

    private static final long serialVersionUID = 9104092580669691633L;

    /** 调用rpc的线程调用点信息 */
    private FromPoint fromPoint;
    /** 目标调用的rpc调用点信息 */
    private CallPoint callPoint;

    /** rpc调用的方法 */
    private String methodName;
    /** rpc调用参数列表 */
    private Object[] methodArgs;

    /** rpc方法 */
    private transient Method method;
    /** 返回值类型 */
    private transient Class<?> returnType;

    public RpcInvocation() {

    }

    public RpcInvocation(FromPoint fromPoint, CallPoint callPoint, Method method, Object[] methodArgs) {
        this.fromPoint = fromPoint;
        this.callPoint = callPoint;
        this.methodName = method.getName();
        this.methodArgs = methodArgs;
        // transient
        this.method = method;
        this.returnType = method.getReturnType();
    }

    public FromPoint getFromPoint() {
        return fromPoint;
    }

    public CallPoint getCallPoint() {
        return callPoint;
    }

    public String getMethodName() {
        return methodName;
    }

    public Object[] getMethodArgs() {
        return methodArgs;
    }

    /**
     * 单向的rpc，返回值为 {@code void} 或者 {@link Void}
     * @return 无返回值（单向的）返回 {@code true}
     */
    public boolean isOneWay() {
        // 这里有点问题，rpc接收端会有问题
        return returnType != null ? (void.class.equals(returnType) || Void.class.equals(returnType)) : false;
    }

    public boolean isCompletableFuture() {
        // 这里有点问题，rpc接收端会有问题
        return returnType != null ? returnType.equals(CompletableFuture.class) : false;
    }
}

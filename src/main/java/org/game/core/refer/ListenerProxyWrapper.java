package org.game.core.refer;

public class ListenerProxyWrapper<T> {

    private final T proxy;

    private final RemoteServiceInvoker remoteServiceInvoker;

    public ListenerProxyWrapper(T proxy, RemoteServiceInvoker remoteServiceInvoker) {
        this.proxy = proxy;
        this.remoteServiceInvoker = remoteServiceInvoker;
    }

    public T getProxy() {
        return proxy;
    }

//    public void listenResult(JowFunction2<Param, Param> method, Param context) {
//        context.put("_callerInfo", remoteServiceInvoker.getToPoint().callerInfo);
//        final Port localPort = Port.getCurrent();
//        assert localPort != null : "Port.getCurrent() is null";
//        localPort.listenResult(method, context);
//    }
//
//    public void setToPoint(CallPoint toPoint) {
//        remoteServiceInvoker.setToPoint(toPoint);
//    }

}

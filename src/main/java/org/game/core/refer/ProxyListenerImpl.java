package org.game.core.refer;

import org.game.core.CallPoint;

public class ProxyListenerImpl implements ProxyListener {

    private final CallPoint toPoint;

    public ProxyListenerImpl(CallPoint toPoint) {
        this.toPoint = toPoint;
    }

//    @Override
//    public void listenResult(JowFunction2<Param, Param> method, Param context) {
//        context.put("_callerInfo", toPoint.callerInfo);
//        final Port localPort = Port.getCurrent();
//        assert localPort != null : "Port.getCurrent() is null";
//        localPort.listenResult(method, context);
//    }
}

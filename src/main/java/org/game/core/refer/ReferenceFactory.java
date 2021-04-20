package org.game.core.refer;

import java.lang.reflect.Proxy;

/**
 * 远程代理创建工厂
 * <p>传入Service的接口类，生成对应的Service代理对象。</p>
 *
 * @author Ziegler
 * date 2021/4/12
 */
public class ReferenceFactory {

    /**
     * 创建Service的代理
     * @param type 要创建的代理类的Class
     * @param <T> 要代理的接口类型
     * @return 返回代理对象
     */
    public static <T> T getProxy(Class<T> type) {
        RemoteServiceInvoker<T> proxy = new RemoteServiceInvoker<>(type);
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{type}, proxy);
    }

    /**
     * 创建包含Service代理的Listener包裹器，支持异步监听返回数据
     * @param type 要创建的代理类的Class
     * @param <T> 要代理的接口类型
     * @return 返回代理对象
     */
    public static <T> ListenerProxyWrapper<T> getProxyWrapper(Class<T> type) {
        RemoteServiceInvoker<T> proxy = new RemoteServiceInvoker<>(type);
        T remoteProxy = (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{type}, proxy);
        return new ListenerProxyWrapper<>(remoteProxy, proxy);
    }


}

package org.game.core.transport;


/**
 * RPC网络传输数据的相关类。
 * <p>当{@link org.game.core.Service} 处于不同的进程 {@link org.game.core.ServiceNode}上面时，需要通过
 * 网络进行编码和解码消息数据。当前需要编码和解码的对象为 {@link org.game.core.exchange.Request} 和 {@link org.game.core.exchange.Response}
 * 这两个对象。</p>
 * <p>网络传输使用的Netty</p>
 * <p>对象的序列化方式使用的 {@code hessian-lite} 第三方开源序列化库</p>
 *
 *
 * */
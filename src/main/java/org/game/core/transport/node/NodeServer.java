package org.game.core.transport.node;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import org.game.core.ServiceNode;
import org.game.core.transport.ExchangeCodec;
import org.game.core.transport.ServerHandler;
import org.game.core.transport.TransportConsts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * node服务器
 * <p>服务端只负责接收消息，不发送消息</p>
 * <h3>消息包结构</h3>
 * <ul>
 *     <li>Length : 4 bytes (Length value = n bytes)</li>
 *     <li>Data : n bytes</li>
 * </ul>
 * <p>消息包头长度只包括数据内容长度，即：Data length。</p>
 *
 * @author Ziegler
 * date 2021/4/13
 */
public class NodeServer {

    /** logger */
    private static final Logger logger = LoggerFactory.getLogger(NodeServer.class);

    private final NioEventLoopGroup group = new NioEventLoopGroup();
    /** 服务端启动器 */
    private final ServerBootstrap serverBootstrap = new ServerBootstrap();

    private final ServiceNode serviceNode;

    public NodeServer(ServiceNode serviceNode) {
        this.serviceNode = serviceNode;
        serverBootstrap.group(group)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new LengthFieldPrepender(TransportConsts.HEAD_LENGTH_FIELD_LENGTH))
                                .addLast(new LengthFieldBasedFrameDecoder(TransportConsts.MAX_FRAME_LENGTH,
                                        0, TransportConsts.HEAD_LENGTH_FIELD_LENGTH,
                                        0, TransportConsts.HEAD_LENGTH_FIELD_LENGTH))
                                .addLast("hessianCodec", new ExchangeCodec())
                                .addLast("server-idle-handler", new IdleStateHandler(0, 0, 10, TimeUnit.SECONDS))
                                .addLast("handler", new ServerHandler(serviceNode));
//                                .addLast(new LengthFieldPrepender(TransportConsts.HEAD_LENGTH_FIELD_LENGTH))
                    }
                });
    }

    public void shutdown() {
        group.shutdownGracefully();
    }

    /**
     * 启动node服务端监听
     * @param port 启动端口
     * @return future对象
     */
    public ChannelFuture start(int port) {
        final ChannelFuture future = serverBootstrap.bind(new InetSocketAddress(port));
        future.addListener(f -> {
            if (f.isSuccess()) {
                logger.info("Server bind. node = {}, port = {}", serviceNode.getName(), port);
            } else {
                logger.error("bind attempt failed. node = {}, port = {}", serviceNode.getName(), port);
            }
        });
        return future;
    }

}

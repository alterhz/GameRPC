package org.game.core.transport;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.game.core.ServiceNode;
import org.game.core.exchange.Request;
import org.game.core.exchange.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * node客户端handler
 * <p>因为node的客户端 {@link java.nio.channels.Channel} 不需要接收数据，所以无需处理消息。值进行数据派发即可。</p>
 * <p>客户端可以不要此Handler</p>
 * @author Ziegler
 * date 2021/4/13
 */
@Deprecated
public class ClientHandler extends ChannelDuplexHandler {
    /** logger */
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);

        if (logger.isTraceEnabled()) {
            logger.trace("ClientHandler.write");
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        if (logger.isTraceEnabled()) {
            logger.trace("ClientHandler.channelActive channel = {}", ctx.channel());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        if (logger.isTraceEnabled()) {
            logger.trace("ClientHandler.channelInactive");
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (logger.isTraceEnabled()) {
            logger.trace("ClientHandler.channelRead");
        }

        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);

        if (logger.isTraceEnabled()) {
            logger.trace("ClientHandler.exceptionCaught e = {}", cause.getMessage());
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);

        if (logger.isTraceEnabled()) {
            logger.trace("ClientHandler.userEventTriggered evt = {}", evt);
        }
    }
}

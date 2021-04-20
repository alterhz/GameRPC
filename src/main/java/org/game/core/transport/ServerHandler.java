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
 * node服务器handler
 * <p>在方法 {@link ServerHandler#channelRead(io.netty.channel.ChannelHandlerContext, java.lang.Object)} 中派发。当前只派发
 * {@link Request} 和 {@link Response}这两种对象类型。</p>
 * @author Ziegler
 * date 2021/4/13
 */
public class ServerHandler extends ChannelDuplexHandler {
    /** logger */
    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    private final ServiceNode serviceNode;

    public ServerHandler(ServiceNode serviceNode) {
        this.serviceNode = serviceNode;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);

        if (logger.isTraceEnabled()) {
            logger.trace("ServerHandler.write");
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        if (logger.isTraceEnabled()) {
            logger.trace("ServerHandler.channelActive channel = {}", ctx.channel());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        if (logger.isTraceEnabled()) {
            logger.trace("ServerHandler.channelInactive");
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (logger.isTraceEnabled()) {
            logger.trace("ServerHandler.channelRead");
            logger.trace("msg type = {}, msg = {}", msg.getClass().getName(), msg);
        }

        if (serviceNode != null) {
            if (msg instanceof Request) {
                final Request request = (Request) msg;
                logger.trace("org.game.core.transport.ServerHandler.channelRead Request");
                serviceNode.dispatchRequest(request);
            } else if (msg instanceof Response) {
                final Response response = (Response) msg;
                logger.trace("org.game.core.transport.ServerHandler.channelRead Response");
                serviceNode.dispatchResponse(response);
            }
        }

        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);

        if (logger.isTraceEnabled()) {
            logger.trace("ServerHandler.exceptionCaught e = {}", cause.getMessage());
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);

        if (logger.isTraceEnabled()) {
            logger.trace("ServerHandler.userEventTriggered evt = {}", evt);
        }
    }
}

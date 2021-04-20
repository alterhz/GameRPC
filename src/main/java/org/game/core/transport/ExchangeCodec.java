package org.game.core.transport;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.game.core.exchange.Request;
import org.game.core.exchange.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 编码和解码
 * <p>主要是 {@link Request}和 {@link Response}对象的编码和解码。</p>
 * <p> 1. 先写入数据类型标记 </p>
 * <ul>
 *     <li>{@link ExchangeCodec#FLAG_STRING} 对应 {@link String}</li>
 *     <li>{@link ExchangeCodec#FLAG_REQUEST} 对应 {@link Request}</li>
 *     <li>{@link ExchangeCodec#FLAG_RESPONSE} 对应 {@link Response}</li>
 * </ul>
 * <p>2. 然后写入真实数据</p>
 * @author Ziegler
 * date 2021/4/13
 */
public class ExchangeCodec extends ByteToMessageCodec {

    public static final byte FLAG_STRING = 0x10;
    public static final byte FLAG_REQUEST = 0x20;
    public static final byte FLAG_RESPONSE = 0x30;

    /** logger */
    private static final Logger logger = LoggerFactory.getLogger(ExchangeCodec.class);


    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (msg instanceof String) {
            final String str = (String) msg;
            out.writeByte(FLAG_STRING);
            final byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
            out.writeBytes(bytes);
        } else if (msg instanceof Request) {
            final Request request = (Request) msg;
            out.writeByte(FLAG_REQUEST);
            final byte[] encode = Hessian2Utils.encode(request);
            out.writeBytes(encode);
        } else if (msg instanceof Response) {
            final Response response = (Response) msg;
            out.writeByte(FLAG_RESPONSE);
            final byte[] encode = Hessian2Utils.encode(response);
            out.writeBytes(encode);
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List out) throws Exception {
        final byte flag = in.readByte();
        final int length = in.readableBytes();
        if (flag == FLAG_STRING) {
            final ByteBuf byteBuf = in.readBytes(length);
            final String str = String.valueOf(byteBuf.toString(StandardCharsets.UTF_8));
            out.add(str);
            logger.debug("接收到消息包长度。length = {}, str = {}", length, str);
        } else if (flag == FLAG_REQUEST) {
            byte[] buffer = new byte[length];
            in.readBytes(buffer);
            final Request request = Hessian2Utils.decode(buffer);
            out.add(request);
            logger.debug("接收到消息包长度。length = {}, request.id = {}", length, request.getId());
        } else if (flag == FLAG_RESPONSE) {
            byte[] buffer = new byte[length];
            in.readBytes(buffer);
            final Response response = Hessian2Utils.decode(buffer);
            out.add(response);
            logger.debug("接收到消息包长度。length = {}, response.id = {}", length, response.getId());
        }
    }
}

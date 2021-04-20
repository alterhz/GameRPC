package org.game.core.transport;

import com.alibaba.com.caucho.hessian.io.Hessian2Input;
import com.alibaba.com.caucho.hessian.io.Hessian2Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 编码和解码方法，使用lite-hessian进行编解码
 *
 * @author Ziegler
 * date 2021/4/12
 */
public class Hessian2Utils {

    /**
     * 编码
     * @param obj 要编码的对象
     * @return 用于传输的编码后的数据
     * @throws IOException 失败
     */
    public static byte[] encode(Object obj) throws IOException {
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        final Hessian2Output hessian2Output = new Hessian2Output(bout);
        hessian2Output.writeObject(obj);
        hessian2Output.flush();
        return bout.toByteArray();
    }

    /**
     * 解码
     * @param buffer 待解码的字符串
     * @return 解码成功返回 {@code true}
     * @throws IOException 失败
     */
    public static <T> T decode(byte[] buffer) throws IOException {
        final ByteArrayInputStream bin = new ByteArrayInputStream(buffer);
        final Hessian2Input hessian2Input = new Hessian2Input(bin);
        return (T)hessian2Input.readObject();
    }
}

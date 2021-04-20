package org.game.core.exchange;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * rpc请求应答对象
 * <p>应答数据保存在 {@code result} 中。</p>
 *
 * @author Ziegler
 * date 2021/4/12
 */
public final class Response implements Serializable {

    private static final long serialVersionUID = 9104092580669691633L;

    private Long id;
    private int status = 0;
    private Object result;

    public Response() {
    }

    public Response(Long id, int status) {
        this.id = id;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("status", status)
                .toString();
    }
}

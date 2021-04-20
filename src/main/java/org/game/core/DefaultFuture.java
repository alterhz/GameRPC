package org.game.core;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.game.core.exchange.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RPC请求 {@link Request}等待应答结果，并处理超时。
 *
 * @author Ziegler
 * date 2021/4/12
 */
public class DefaultFuture extends CompletableFuture<Object> {

    private static final Map<Long, DefaultFuture> FUTURES = new ConcurrentHashMap<>();

    /** RequestId */
    private final Long id;
    /** 请求对象 */
    private final Request request;
    /** 超时时间：超过这个时间就是超时。 */
    private final long timeout;

    /** logger */
    private static final Logger logger = LoggerFactory.getLogger(DefaultFuture.class);

    private DefaultFuture(Request request, long timeout) {
        this.id = request.getId();
        this.request = request;
        this.timeout = System.currentTimeMillis() + timeout;
    }

    /**
     * 已超时
     * @return 超时返回 {@code true}
     */
    public boolean isTimeout() {
        return System.currentTimeMillis() > timeout;
    }

    @Override
    public boolean complete(Object value) {
        FUTURES.remove(id);
        logger.debug("DefaultFuture.complete = {}", id);
        return super.complete(value);
    }

    @Override
    public boolean completeExceptionally(Throwable ex) {
        FUTURES.remove(id);
        logger.debug("DefaultFuture.completeExceptionally = {}", id);
        return super.completeExceptionally(ex);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        FUTURES.remove(id);
        logger.debug("DefaultFuture.cancel = {}", id);
        return super.cancel(mayInterruptIfRunning);
    }

    public static DefaultFuture newFuture(Request request, long timeout) {
        final DefaultFuture future = new DefaultFuture(request, timeout);
        FUTURES.put(future.getId(), future);
        // 在当前Thread添加计时器，处理超时
        ServicePort.getServicePort().addFutureTimeoutTask(future);
        return future;
    }

    public static DefaultFuture getFuture(long id) {
        return FUTURES.get(id);
    }

    public Long getId() {
        return id;
    }

    public Request getRequest() {
        return request;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("timeout", timeout)
                .toString();
    }
}

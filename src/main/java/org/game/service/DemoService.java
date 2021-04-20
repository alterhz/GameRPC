package org.game.service;

import java.util.concurrent.CompletableFuture;

import org.game.global.ServiceConsts;
import org.game.core.ServiceConfig;
import org.game.provider.DemoServiceImpl;

@ServiceConfig(node = ServiceConsts.NODE0,
        port = ServiceConsts.PORT1,
        serviceImplType = DemoServiceImpl.class)
public interface DemoService {
    /**
     * 无返回值的PRC调用
     */
    void test();

    /**
     * 异步返回值的RPC调用
     */
    CompletableFuture<String> getServiceName();

    /**
     * 阻塞RPC调用
     */
    Integer getId();
}

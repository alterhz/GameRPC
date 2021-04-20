package org.game.provider;

import org.game.core.ServiceBase;
import org.game.core.refer.ReferenceFactory;
import org.game.service.DemoService;
import org.game.service.InitService;
import org.game.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class InitServiceImpl extends ServiceBase implements InitService {

    /** logger */
    private static final Logger logger = LoggerFactory.getLogger(InitServiceImpl.class);

    @Override
    public void init() {
        logger.trace("InitServiceImpl.init");

        logger.info("1. RPC调用demoService.test()");
        final DemoService demoService = ReferenceFactory.getProxy(DemoService.class);
        demoService.test();

        logger.info("2. RPC调用demoService.getServiceName()");
        final CompletableFuture<String> future = demoService.getServiceName();
        future.whenComplete((s, throwable) -> {
            // 异步处理
            logger.info("2. RPC返回结果：Service名称 = " + s);
        });

        logger.info("3. RPC阻塞调用demoService.getId()");
        final Integer id = demoService.getId();
        logger.info("3. RPC阻塞调用返回。id = {}", id);
    }
}

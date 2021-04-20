package org.game.provider;

import org.game.core.ServiceBase;
import org.game.service.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class DemoServiceImpl extends ServiceBase implements DemoService {

    /** logger */
    private static final Logger logger = LoggerFactory.getLogger(DemoServiceImpl.class);

    @Override
    public void test() {
        logger.info("1. 执行DemoServiceImpl.test");
    }

    @Override
    public CompletableFuture<String> getServiceName() {
        return CompletableFuture.completedFuture("DemoServiceImpl");
    }

    @Override
    public Integer getId() {
        return 192;
    }

    @Override
    public void init() {
        logger.trace("DemoServiceImpl.init");
    }
}

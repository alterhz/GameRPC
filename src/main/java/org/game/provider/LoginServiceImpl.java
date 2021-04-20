package org.game.provider;

import org.game.core.ServiceBase;
import org.game.core.refer.ReferenceFactory;
import org.game.service.DemoService;
import org.game.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class LoginServiceImpl extends ServiceBase implements LoginService {

    /** logger */
    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    private int loginId = 100;

    @Override public CompletableFuture<Integer> login() {
        logger.trace("LoginServiceImpl.login");
        return CompletableFuture.completedFuture(20);
    }

    @Override
    public Integer allocLoginId() {
        return loginId++;
    }

    @Override public void init() {
        logger.trace("LoginServiceImpl.init");

        if (false) {
            logger.info("loginService.login()");
            final LoginService loginService = ReferenceFactory.getProxy(LoginService.class);
            final CompletableFuture<Integer> loginResult = loginService.login();
            loginResult.whenComplete((value, throwable) -> logger.info("RPC返回结果：login result = {}", value));

            logger.info("阻塞调用loginService.login()");
            final long t1 = System.currentTimeMillis();
            final Integer loginId = loginService.allocLoginId();
            final long t2 = System.currentTimeMillis();
            logger.info("阻塞调用返回。耗时 = {}毫秒, loginId = {}", (t2 - t1), loginId);
        }
    }
}

package org.game.service;

import org.game.global.ServiceConsts;
import org.game.core.ServiceConfig;
import org.game.provider.LoginServiceImpl;

import java.util.concurrent.CompletableFuture;

@ServiceConfig(node = ServiceConsts.NODE0,
        port = ServiceConsts.PORT2,
        serviceImplType = LoginServiceImpl.class)
public interface LoginService {
    CompletableFuture<Integer> login();

    Integer allocLoginId();
}

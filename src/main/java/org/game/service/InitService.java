package org.game.service;

import org.game.global.ServiceConsts;
import org.game.core.ServiceConfig;
import org.game.provider.InitServiceImpl;

@ServiceConfig(node = ServiceConsts.NODE0,
        port = ServiceConsts.PORT0,
        serviceImplType = InitServiceImpl.class)
public interface InitService {

}

package org.game;

import org.game.core.ServiceNode;
import org.game.global.ServiceConsts;
import org.game.global.ServiceUtils;
import org.game.service.DemoService;
import org.game.service.InitService;
import org.game.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Executors;

/**
 * 启动类
 *
 * @author Ziegler
 * date 2021/4/14
 */
public class StartUp {

    /** logger */
    private static final Logger logger = LoggerFactory.getLogger(StartUp.class);

    public static void main(String[] args) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, InterruptedException {
        final StartUp startUp = new StartUp();
        startUp.run(args);
    }

    /**
     * 启动参数 node0 port0
     */
    void run(String args[]) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, InterruptedException {
        if (logger.isTraceEnabled()) {
            logger.info("StartUp");
        }

        final ServiceNode serviceNode = new ServiceNode(ServiceConsts.NODE0, Executors.newCachedThreadPool());
        serviceNode.init();

        // 等待1秒建立连接
        Thread.sleep(1000);

        ServiceUtils.addService(serviceNode, InitService.class);
        ServiceUtils.addService(serviceNode, DemoService.class);
        ServiceUtils.addService(serviceNode, LoginService.class);

        serviceNode.startAllService();

        logger.debug("RPC_ALWAYS_USE_TRANSPORT = {}", ServiceConsts.RPC_ALWAYS_USE_TRANSPORT);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // TODO 关闭还是有问题的，没有处理好
            serviceNode.shutdown();
        }));

    }
}

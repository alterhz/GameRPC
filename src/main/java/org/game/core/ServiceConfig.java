package org.game.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务启动注解
 *
 * @author Ziegler
 * date 2021/4/12
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ServiceConfig {

    /**
     * 启动的目标node，服务进程
     */
    String node();

    /**
     * 启动的目标port，服务线程
     */
    String port();

    /**
     * 服务实例类，用来创建服务器的启动实例
     */
    Class<? extends Service> serviceImplType();
}

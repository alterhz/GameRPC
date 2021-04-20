package org.game.core;

/**
 * Service服务基类
 *
 * @author Ziegler
 * date 2021/3/8
 */
public interface Service {

    /**
     * 初始化方法
     */
    void init();

    /**
     * 心跳
     * @param now 时间戳
     */
    void pulse(long now);
}

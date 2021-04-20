package org.game.core;

import java.util.concurrent.TimeUnit;

/**
 * Service服务器的基类，处理一部分通用的逻辑。例如：心跳
 *
 * @author Ziegler
 * date 2021/4/12
 */
public abstract class ServiceBase implements Service {

    private long nextSecondPulse;

    @Override
    public void pulse(long now) {
        if (now > nextSecondPulse) {
            nextSecondPulse += TimeUnit.SECONDS.toMillis(1L);
            pulseEverySecond(now);
        }
    }

    /**
     * 每秒一次的心跳
     * @param now 时间戳
     */
    protected void pulseEverySecond(long now) {

    }
}

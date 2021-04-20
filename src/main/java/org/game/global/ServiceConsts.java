package org.game.global;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service相关常量定义
 *
 * @author Ziegler
 * date 2021/2/4
 */
public class ServiceConsts {

    /**
     * node列表
     */
    public static final String NODE0 = "node0";
    public static final String NODE1 = "node1";
    public static final String NODE2 = "node2";

    /** 节点配置信息 */
    public static final Map<String, Pair<String, Integer>> NODE_CONFIGS = new ConcurrentHashMap<>();
    static {
        NODE_CONFIGS.put(NODE0, ImmutablePair.of("127.0.0.1", 8801));
        NODE_CONFIGS.put(NODE1, ImmutablePair.of("127.0.0.1", 8802));
        NODE_CONFIGS.put(NODE2, ImmutablePair.of("127.0.0.1", 8803));
    }

    /**
     * port列表
     */
    public static final String PORT0 = "port0";
    public static final String PORT1 = "port1";
    public static final String PORT2 = "port2";

    /** rpc总是使用网络传输方式 */
    public static final boolean RPC_ALWAYS_USE_TRANSPORT = true;

    private ServiceConsts() {
        throw new AssertionError();
    }

}

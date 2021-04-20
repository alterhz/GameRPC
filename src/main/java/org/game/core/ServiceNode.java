package org.game.core;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.game.core.exchange.Request;
import org.game.core.exchange.Response;
import org.game.core.transport.node.NodeClient;
import org.game.core.transport.node.NodeServer;
import org.game.global.ServiceConsts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * 服务节点
 * <p>服务节点，对应进程，每个进程对应一个ServiceNode。一个ServiceNode下面可以挂载或管理若干个ServicePort。</p>
 * @author Ziegler
 * date 2021/4/12
 */
public class ServiceNode {

    private final String name;
    /** 服务器线程列表 */
    private final Map<String, ServicePort> servicePorts = new ConcurrentHashMap<>();

    /** 远端的 {@link ServiceNode} */
    private final Map<String, NodeClient> nodeClients = new ConcurrentHashMap<>();

    /** 空的node连接对象 */
    private static final NodeClient.EmptyNodeClient EMPTY_NODE_CLIENT = new NodeClient.EmptyNodeClient("empty");

    /** 服务端节点 */
    private final NodeServer nodeServer = new NodeServer(this);

    /** {@link ServicePort} 执行线程池 */
    private final ExecutorService executorService;

    /** logger */
    private static final Logger logger = LoggerFactory.getLogger(ServiceNode.class);

    public ServiceNode(String name, ExecutorService executorService) {
        this.name = name;
        this.executorService = executorService;
    }

    /**
     * TODO code {@link NodeClient} 断线重连检测
     */
    public void checkChannelActive() {
        ServiceConsts.NODE_CONFIGS.forEach((node, ipPort) -> {
            boolean isConnected = isConnected(node);
            if (!isConnected) {
                // 连接已经断开
                connectNode(node, ipPort.getLeft(), ipPort.getRight());
            }
        });
    }

    private boolean isConnected(String node) {
        final NodeClient nodeClient = nodeClients.get(node);
        return nodeClient != null ? nodeClient.isActive() : false;
    }

    public void shutdown() {
        executorService.shutdownNow();
        nodeServer.shutdown();
        final Iterator<NodeClient> iterator = nodeClients.values().iterator();
        while (iterator.hasNext()) {
            final NodeClient nodeClient = iterator.next();
            nodeClient.shutdown();
        }
    }

    public void init() {
        final Pair<String, Integer> nodeConfig = ServiceConsts.NODE_CONFIGS.get(name);
        if (nodeConfig == null) {
            logger.error("node 启动失败，未获取到启动端口。name = {}", name);
            return;
        }

        nodeServer.start(nodeConfig.getRight());

        // 启动NodeClient连接远程node，间隔10秒检测一次连接状态，断开重连
        executorService.execute(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                checkChannelActive();

                try {
                    Thread.sleep(10 * 1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取Node通信对象
     * @param node 名称
     * @return node的通信对象
     */
    public NodeClient getNode(String node) {
        return nodeClients.getOrDefault(node, EMPTY_NODE_CLIENT);
    }

    /**
     * 连接服务端node
     * @param node 名称
     * @param ip 服务端ip
     * @param port 服务端port
     */
    private void connectNode(String node, String ip, int port) {
        final NodeClient nodeClient = new NodeClient(node);
        nodeClients.put(node, nodeClient);
        nodeClient.connect(ip, port);
    }

    /**
     * 派发{@link Request}到对应的{@link ServicePort}
     * @param request rpc请求对象
     */
    public void dispatchRequest(Request request) {
        final RpcInvocation rpcInvocation = request.getRpcInvocation();
        final String portName = rpcInvocation.getCallPoint().getPort();
        final ServicePort servicePort = servicePorts.get(portName);
        if (servicePort == null) {
            logger.error("dispatchRequest. 查找的servicePort不存在。portName = {}", portName);
            return;
        }
        servicePort.addRequest(request);
    }

    /**
     * 派发{@link Response}应答对象
     * @param response 应答对象
     */
    public void dispatchResponse(Response response) {
        final DefaultFuture future = DefaultFuture.getFuture(response.getId());
        if (future == null) {
            logger.error("dispatchResponse. future == null. response = {}", response);
            return;
        }
        final RpcInvocation rpcInvocation = future.getRequest().getRpcInvocation();
        // 发送RPC调用的ServicePort线程
        final String portName = rpcInvocation.getFromPoint().getPort();
        final ServicePort servicePort = servicePorts.get(portName);
        if (servicePort == null) {
            logger.error("dispatchResponse. 查找的servicePort不存在。portName = {}", portName);
            return;
        }
        servicePort.addResponse(response);
    }

    public void addServicePort(ServicePort servicePort) {
        if (servicePorts.containsKey(servicePort.getName())) {
            return;
        }
        servicePorts.put(servicePort.getName(), servicePort);
    }

    /**
     * 通过服务Port名称获取
     * @param portName port名称
     * @return 没有匹配到返回 {@code null}
     */
    public ServicePort getServicePort(String portName) {
        return servicePorts.get(portName);
    }

    public void startAllService() {
        final Iterator<ServicePort> iterator = servicePorts.values().iterator();
        while (iterator.hasNext()) {
            final ServicePort servicePort = iterator.next();
            executorService.execute(servicePort);
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", name)
                .toString();
    }
}

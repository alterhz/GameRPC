package org.game.core.transport;

import org.game.core.ServiceNode;
import org.game.core.transport.node.NodeClient;
import org.game.core.transport.node.NodeServer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RpcStartTest {
    public static void main(String[] args) {
        final int port = 8000;
        final ServiceNode serviceNode = new ServiceNode("node0", null);
        final NodeServer nodeServer = new NodeServer(serviceNode);
        nodeServer.start(port);

        final NodeClient nodeClient = new NodeClient("node0");
        nodeClient.connect("127.0.0.1", port);
    }
}
package org.game.core;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * RPC调用点信息
 *
 * @author Ziegler
 * date 2021/4/12
 */
public final class CallPoint implements Serializable {

    private static final long serialVersionUID = 9104092580669691633L;

    private final String node;
    private final String port;
    private final String service;

    public CallPoint(String node, String port, String service) {
        this.node = node;
        this.port = port;
        this.service = service;
    }

    public String getNode() {
        return node;
    }

    public String getPort() {
        return port;
    }

    public String getService() {
        return service;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CallPoint callPoint = (CallPoint) o;

        return new EqualsBuilder().append(node, callPoint.node)
                .append(port, callPoint.port)
                .append(service, callPoint.service)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(node)
                .append(port)
                .append(service)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("node", node)
                .append("port", port)
                .append("service", service)
                .toString();
    }
}

package org.game.hessian;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Book implements java.io.Serializable {
    private String name;

    public Book(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override public String toString() {
        return new ToStringBuilder(this).append("name", name).toString();
    }
}

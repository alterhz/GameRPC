package org.game.hessian;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Student implements java.io.Serializable {
    private int id;
    private String name;
    private List<Book> books;

    public Student(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    @Override public String toString() {
        return new ToStringBuilder(this).append("id", id)
                                        .append("name", name)
                                        .append("books", books)
                                        .toString();
    }
}

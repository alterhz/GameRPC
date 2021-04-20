package org.game.hessian;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.alibaba.com.caucho.hessian.io.Hessian2Input;
import com.alibaba.com.caucho.hessian.io.Hessian2Output;

public class Test {

    @org.junit.jupiter.api.Test void test1() throws IOException {
        final Student student = new Student(1001, "jack");
        List<Book> books = new ArrayList<>();
        books.add(new Book("java"));
        books.add(new Book("c++"));
        student.setBooks(books);
        System.out.println("student = " + student);

        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        final Hessian2Output hessian2Output = new Hessian2Output(bout);
        hessian2Output.writeObject(student);
        hessian2Output.flush();

        final byte[] bytes = bout.toByteArray();

        final ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
        final Hessian2Input hessian2Input = new Hessian2Input(bin);
        final Student student1 = (Student)hessian2Input.readObject();
        System.out.println("student1 = " + student1);
    }

    @org.junit.jupiter.api.Test
    void testTimeUnit() {
        final long l = TimeUnit.SECONDS.toMillis(2);
        System.out.println("l = " + l);
    }
}

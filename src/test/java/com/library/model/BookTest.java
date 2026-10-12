package com.library.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    @Test
    public void testBookCreation() {
        Book book = new Book("DevOps Handbook", "Gene Kim", "Technology", 10, 8);

        assertEquals("DevOps Handbook", book.getTitle());
        assertEquals("Gene Kim", book.getAuthor());
        assertEquals("Technology", book.getCategory());
        assertEquals(10, book.getQuantity());
        assertEquals(8, book.getAvailableQuantity());
    }

    @Test
    public void testBookSetters() {
        Book book = new Book();
        book.setId(1);
        book.setTitle("Effective Java");
        book.setAuthor("Joshua Bloch");
        book.setCategory("Programming");
        book.setQuantity(5);
        book.setAvailableQuantity(5);

        assertEquals(1, book.getId());
        assertEquals("Effective Java", book.getTitle());
        assertEquals("Joshua Bloch", book.getAuthor());
        assertEquals("Programming", book.getCategory());
        assertEquals(5, book.getQuantity());
        assertEquals(5, book.getAvailableQuantity());
    }
}

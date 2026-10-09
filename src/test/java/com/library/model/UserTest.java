package com.library.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    @DisplayName("User Model: Test constructors, getters, setters, and toString")
    void testUserProperties() {
        // Default Constructor
        User user1 = new User();
        user1.setId(10);
        user1.setName("Alice");
        user1.setEmail("alice@test.com");
        user1.setPassword("secret123");
        user1.setRole("STUDENT");

        assertEquals(10, user1.getId());
        assertEquals("Alice", user1.getName());
        assertEquals("alice@test.com", user1.getEmail());
        assertEquals("secret123", user1.getPassword());
        assertEquals("STUDENT", user1.getRole());

        // Parameterized Constructor without ID
        User user2 = new User("Bob", "bob@test.com", "pass456", "ADMIN");
        assertEquals("Bob", user2.getName());
        assertEquals("bob@test.com", user2.getEmail());
        assertEquals("pass456", user2.getPassword());
        assertEquals("ADMIN", user2.getRole());

        // Parameterized Constructor with ID
        User user3 = new User(20, "Charlie", "charlie@test.com", "pass789", "STUDENT");
        assertEquals(20, user3.getId());
        assertEquals("Charlie", user3.getName());
        assertEquals("charlie@test.com", user3.getEmail());
        assertEquals("pass789", user3.getPassword());
        assertEquals("STUDENT", user3.getRole());

        // toString verification
        String str = user3.toString();
        assertTrue(str.contains("Charlie"));
        assertTrue(str.contains("charlie@test.com"));
        assertTrue(str.contains("STUDENT"));
    }
}

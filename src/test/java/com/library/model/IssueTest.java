package com.library.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

public class IssueTest {

    @Test
    @DisplayName("Issue Model: Test constructors, getters, setters, and toString")
    void testIssueProperties() {
        Date issueDate = Date.valueOf("2026-10-10");
        Date returnDate = Date.valueOf("2026-10-15");

        // 1. Default constructor
        Issue issue1 = new Issue();
        issue1.setId(1);
        issue1.setUserId(10);
        issue1.setBookId(100);
        issue1.setIssueDate(issueDate);
        issue1.setReturnDate(returnDate);
        issue1.setStatus("ISSUED");
        issue1.setBookTitle("Clean Code");
        issue1.setBookAuthor("Robert C. Martin");
        issue1.setUserName("John Doe");
        issue1.setUserEmail("john@example.com");

        assertEquals(1, issue1.getId());
        assertEquals(10, issue1.getUserId());
        assertEquals(100, issue1.getBookId());
        assertEquals(issueDate, issue1.getIssueDate());
        assertEquals(returnDate, issue1.getReturnDate());
        assertEquals("ISSUED", issue1.getStatus());
        assertEquals("Clean Code", issue1.getBookTitle());
        assertEquals("Robert C. Martin", issue1.getBookAuthor());
        assertEquals("John Doe", issue1.getUserName());
        assertEquals("john@example.com", issue1.getUserEmail());

        // 2. Parameterized constructor without id
        Issue issue2 = new Issue(20, 200, issueDate, null, "ISSUED");
        assertEquals(20, issue2.getUserId());
        assertEquals(200, issue2.getBookId());
        assertEquals(issueDate, issue2.getIssueDate());
        assertNull(issue2.getReturnDate());
        assertEquals("ISSUED", issue2.getStatus());

        // 3. Parameterized constructor with all fields
        Issue issue3 = new Issue(2, 30, 300, issueDate, returnDate, "RETURNED",
                "Design Patterns", "GoF", "Jane Student", "jane@example.com");
        assertEquals(2, issue3.getId());
        assertEquals(30, issue3.getUserId());
        assertEquals("RETURNED", issue3.getStatus());
        assertEquals("Design Patterns", issue3.getBookTitle());

        // 4. toString
        String str = issue3.toString();
        assertTrue(str.contains("Design Patterns"));
        assertTrue(str.contains("Jane Student"));
        assertTrue(str.contains("RETURNED"));
    }
}

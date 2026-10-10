package com.library.servlet;

import com.library.dao.IssueDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

public class IssueBookServletTest {

    private IssueBookServlet servlet;

    @Mock
    private IssueDAO issueDAO;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    private static final String CONTEXT_PATH = "/LibraryManagementSystem";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        servlet = new IssueBookServlet();
        servlet.setIssueDAO(issueDAO);
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
    }

    @Test
    @DisplayName("IssueBook: Unauthenticated user is redirected to login")
    void testUnauthenticatedUser() throws ServletException, IOException {
        when(request.getSession(false)).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendRedirect(CONTEXT_PATH + "/login.jsp");
        verify(issueDAO, never()).issueBook(anyInt(), anyInt());
    }

    @Test
    @DisplayName("IssueBook: Non-student role is redirected to login")
    void testNonStudentRole() throws ServletException, IOException {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("role")).thenReturn("ADMIN");
        when(session.getAttribute("userId")).thenReturn(1);

        servlet.doPost(request, response);

        verify(response).sendRedirect(CONTEXT_PATH + "/login.jsp");
        verify(issueDAO, never()).issueBook(anyInt(), anyInt());
    }

    @Test
    @DisplayName("IssueBook: Successfully issues book and redirects to issued-books.jsp")
    void testIssueBookSuccess() throws ServletException, IOException {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("role")).thenReturn("STUDENT");
        when(session.getAttribute("userId")).thenReturn(5);
        when(request.getParameter("book_id")).thenReturn("12");
        when(issueDAO.issueBook(5, 12)).thenReturn(true);

        servlet.doPost(request, response);

        verify(issueDAO).issueBook(5, 12);
        verify(response).sendRedirect(contains("/student/issued-books.jsp?success="));
    }

    @Test
    @DisplayName("IssueBook: Fails when book is out of stock or already issued")
    void testIssueBookFailure() throws ServletException, IOException {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("role")).thenReturn("STUDENT");
        when(session.getAttribute("userId")).thenReturn(5);
        when(request.getParameter("book_id")).thenReturn("12");
        when(issueDAO.issueBook(5, 12)).thenReturn(false);

        servlet.doPost(request, response);

        verify(issueDAO).issueBook(5, 12);
        verify(response).sendRedirect(contains("/student/books.jsp?error="));
    }

    @Test
    @DisplayName("IssueBook: Invalid book ID redirects with error")
    void testInvalidBookId() throws ServletException, IOException {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("role")).thenReturn("STUDENT");
        when(session.getAttribute("userId")).thenReturn(5);
        when(request.getParameter("book_id")).thenReturn("abc");

        servlet.doPost(request, response);

        verify(issueDAO, never()).issueBook(anyInt(), anyInt());
        verify(response).sendRedirect(contains("/student/books.jsp?error="));
    }
}

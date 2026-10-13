package com.library.servlet;

import com.library.dao.IssueDAO;
import com.library.model.Issue;
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
import java.sql.Date;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

public class ReturnBookServletTest {

    private ReturnBookServlet servlet;

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
        servlet = new ReturnBookServlet();
        servlet.setIssueDAO(issueDAO);
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
    }

    @Test
    @DisplayName("ReturnBook: Unauthenticated user is redirected to login")
    void testUnauthenticatedUser() throws ServletException, IOException {
        when(request.getSession(false)).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendRedirect(CONTEXT_PATH + "/login.jsp");
        verify(issueDAO, never()).returnBook(anyInt());
    }

    @Test
    @DisplayName("ReturnBook: Student returns their own issued book successfully")
    void testStudentReturnBookSuccess() throws ServletException, IOException {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("role")).thenReturn("STUDENT");
        when(session.getAttribute("userId")).thenReturn(5);
        when(request.getParameter("issue_id")).thenReturn("3");

        Issue issue = new Issue(3, 5, 10, Date.valueOf("2026-10-01"), null, "ISSUED");
        when(issueDAO.getIssueById(3)).thenReturn(issue);
        when(issueDAO.returnBook(3)).thenReturn(true);

        servlet.doPost(request, response);

        verify(issueDAO).returnBook(3);
        verify(response).sendRedirect(contains("/student/issued-books.jsp?success="));
    }

    @Test
    @DisplayName("ReturnBook: Student cannot return another student's book")
    void testStudentReturnUnauthorized() throws ServletException, IOException {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("role")).thenReturn("STUDENT");
        when(session.getAttribute("userId")).thenReturn(5);
        when(request.getParameter("issue_id")).thenReturn("3");

        // Issue belongs to userId 9, not 5
        Issue issue = new Issue(3, 9, 10, Date.valueOf("2026-10-01"), null, "ISSUED");
        when(issueDAO.getIssueById(3)).thenReturn(issue);

        servlet.doPost(request, response);

        verify(issueDAO, never()).returnBook(3);
        verify(response).sendRedirect(contains("/student/issued-books.jsp?error="));
    }

    @Test
    @DisplayName("ReturnBook: Admin can mark any book returned")
    void testAdminReturnBookSuccess() throws ServletException, IOException {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("role")).thenReturn("ADMIN");
        when(session.getAttribute("userId")).thenReturn(1);
        when(request.getParameter("issue_id")).thenReturn("7");
        when(issueDAO.returnBook(7)).thenReturn(true);

        servlet.doPost(request, response);

        verify(issueDAO).returnBook(7);
        verify(response).sendRedirect(contains("/admin/issued-books.jsp?success="));
    }
}

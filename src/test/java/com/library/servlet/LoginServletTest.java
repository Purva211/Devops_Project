package com.library.servlet;

import com.library.dao.UserDAO;
import com.library.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class LoginServletTest {

    private LoginServlet servlet;

    @Mock
    private UserDAO userDAO;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher requestDispatcher;

    private static final String CONTEXT_PATH = "/LibraryManagementSystem";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        servlet = new LoginServlet();
        servlet.setUserDAO(userDAO);
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getRequestDispatcher("login.jsp")).thenReturn(requestDispatcher);
    }

    @Test
    @DisplayName("Login: Correct ADMIN credentials create session and redirect to admin dashboard")
    void testAdminLoginSuccess() throws ServletException, IOException {
        User adminUser = new User(1, "Admin User", "admin@library.com", "admin123", "ADMIN");
        when(request.getParameter("email")).thenReturn("admin@library.com");
        when(request.getParameter("password")).thenReturn("admin123");
        when(userDAO.loginUser("admin@library.com", "admin123")).thenReturn(adminUser);
        when(request.getSession(true)).thenReturn(session);

        servlet.doPost(request, response);

        verify(session).setAttribute("userId", 1);
        verify(session).setAttribute("name", "Admin User");
        verify(session).setAttribute("email", "admin@library.com");
        verify(session).setAttribute("role", "ADMIN");

        verify(response).sendRedirect(CONTEXT_PATH + "/admin/dashboard.jsp");
    }

    @Test
    @DisplayName("Login: Correct STUDENT credentials create session and redirect to student dashboard")
    void testStudentLoginSuccess() throws ServletException, IOException {
        User studentUser = new User(2, "Student Jane", "jane@library.com", "pass123", "STUDENT");
        when(request.getParameter("email")).thenReturn("jane@library.com");
        when(request.getParameter("password")).thenReturn("pass123");
        when(userDAO.loginUser("jane@library.com", "pass123")).thenReturn(studentUser);
        when(request.getSession(true)).thenReturn(session);

        servlet.doPost(request, response);

        verify(session).setAttribute("userId", 2);
        verify(session).setAttribute("name", "Student Jane");
        verify(session).setAttribute("email", "jane@library.com");
        verify(session).setAttribute("role", "STUDENT");

        verify(response).sendRedirect(CONTEXT_PATH + "/student/dashboard.jsp");
    }

    @Test
    @DisplayName("Login: Wrong password displays error")
    void testWrongPassword() throws ServletException, IOException {
        when(request.getParameter("email")).thenReturn("admin@library.com");
        when(request.getParameter("password")).thenReturn("wrongpass");
        when(userDAO.loginUser("admin@library.com", "wrongpass")).thenReturn(null);

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errorMessage"), contains("Invalid email or password"));
        verify(requestDispatcher).forward(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    @DisplayName("Login: Non-existent email displays error")
    void testNonExistentEmail() throws ServletException, IOException {
        when(request.getParameter("email")).thenReturn("nonexistent@library.com");
        when(request.getParameter("password")).thenReturn("somepass");
        when(userDAO.loginUser("nonexistent@library.com", "somepass")).thenReturn(null);

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errorMessage"), contains("Invalid email or password"));
        verify(requestDispatcher).forward(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    @DisplayName("Login: Empty email field displays error")
    void testEmptyEmail() throws ServletException, IOException {
        when(request.getParameter("email")).thenReturn("");
        when(request.getParameter("password")).thenReturn("password123");

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errorMessage"), contains("Please provide both email and password"));
        verify(requestDispatcher).forward(request, response);
        verify(userDAO, never()).loginUser(anyString(), anyString());
    }

    @Test
    @DisplayName("Login: Empty password field displays error")
    void testEmptyPassword() throws ServletException, IOException {
        when(request.getParameter("email")).thenReturn("user@library.com");
        when(request.getParameter("password")).thenReturn("");

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errorMessage"), contains("Please provide both email and password"));
        verify(requestDispatcher).forward(request, response);
        verify(userDAO, never()).loginUser(anyString(), anyString());
    }
}

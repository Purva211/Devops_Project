package com.library.servlet;

import com.library.dao.UserDAO;
import com.library.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class RegisterServletTest {

    private RegisterServlet servlet;

    @Mock
    private UserDAO userDAO;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher requestDispatcher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        servlet = new RegisterServlet();
        servlet.setUserDAO(userDAO);
        when(request.getRequestDispatcher("register.jsp")).thenReturn(requestDispatcher);
    }

    @Test
    @DisplayName("Registration: Valid registration saves student and redirects to login.jsp")
    void testValidRegistration() throws ServletException, IOException {
        when(request.getParameter("name")).thenReturn("John Doe");
        when(request.getParameter("email")).thenReturn("john@example.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getParameter("confirmPassword")).thenReturn("password123");
        when(userDAO.isEmailRegistered("john@example.com")).thenReturn(false);
        when(userDAO.registerUser(any(User.class))).thenReturn(true);

        servlet.doPost(request, response);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDAO).registerUser(userCaptor.capture());
        User captured = userCaptor.getValue();
        assertEquals("John Doe", captured.getName());
        assertEquals("john@example.com", captured.getEmail());
        assertEquals("password123", captured.getPassword());
        assertEquals("STUDENT", captured.getRole());

        verify(response).sendRedirect(contains("login.jsp?success="));
    }

    @Test
    @DisplayName("Registration: Empty name displays error")
    void testEmptyName() throws ServletException, IOException {
        when(request.getParameter("name")).thenReturn("");
        when(request.getParameter("email")).thenReturn("john@example.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getParameter("confirmPassword")).thenReturn("password123");

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errorMessage"), eq("All fields are required."));
        verify(requestDispatcher).forward(request, response);
        verify(userDAO, never()).registerUser(any());
    }

    @Test
    @DisplayName("Registration: Empty email displays error")
    void testEmptyEmail() throws ServletException, IOException {
        when(request.getParameter("name")).thenReturn("John Doe");
        when(request.getParameter("email")).thenReturn("   ");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getParameter("confirmPassword")).thenReturn("password123");

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errorMessage"), eq("All fields are required."));
        verify(requestDispatcher).forward(request, response);
        verify(userDAO, never()).registerUser(any());
    }

    @Test
    @DisplayName("Registration: Invalid email format displays error")
    void testInvalidEmail() throws ServletException, IOException {
        when(request.getParameter("name")).thenReturn("John Doe");
        when(request.getParameter("email")).thenReturn("invalid-email-format");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getParameter("confirmPassword")).thenReturn("password123");

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errorMessage"), eq("Please enter a valid email address."));
        verify(requestDispatcher).forward(request, response);
        verify(userDAO, never()).registerUser(any());
    }

    @Test
    @DisplayName("Registration: Empty password displays error")
    void testEmptyPassword() throws ServletException, IOException {
        when(request.getParameter("name")).thenReturn("John Doe");
        when(request.getParameter("email")).thenReturn("john@example.com");
        when(request.getParameter("password")).thenReturn("");
        when(request.getParameter("confirmPassword")).thenReturn("");

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errorMessage"), eq("All fields are required."));
        verify(requestDispatcher).forward(request, response);
        verify(userDAO, never()).registerUser(any());
    }

    @Test
    @DisplayName("Registration: Password mismatch displays error")
    void testPasswordMismatch() throws ServletException, IOException {
        when(request.getParameter("name")).thenReturn("John Doe");
        when(request.getParameter("email")).thenReturn("john@example.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getParameter("confirmPassword")).thenReturn("different456");

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errorMessage"), eq("Passwords do not match."));
        verify(requestDispatcher).forward(request, response);
        verify(userDAO, never()).registerUser(any());
    }

    @Test
    @DisplayName("Registration: Duplicate email displays error")
    void testDuplicateEmail() throws ServletException, IOException {
        when(request.getParameter("name")).thenReturn("John Doe");
        when(request.getParameter("email")).thenReturn("john@example.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getParameter("confirmPassword")).thenReturn("password123");
        when(userDAO.isEmailRegistered("john@example.com")).thenReturn(true);

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errorMessage"), contains("already exists"));
        verify(requestDispatcher).forward(request, response);
        verify(userDAO, never()).registerUser(any());
    }
}

package com.library.servlet;

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

import static org.mockito.Mockito.*;

public class LogoutServletTest {

    private LogoutServlet servlet;

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
        servlet = new LogoutServlet();
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
    }

    @Test
    @DisplayName("Logout: Session is invalidated and user is redirected to login.jsp via GET")
    void testLogoutWithActiveSessionGet() throws ServletException, IOException {
        when(request.getSession(false)).thenReturn(session);

        servlet.doGet(request, response);

        verify(session).invalidate();
        verify(response).sendRedirect(CONTEXT_PATH + "/login.jsp");
    }

    @Test
    @DisplayName("Logout: Session is invalidated and user is redirected to login.jsp via POST")
    void testLogoutWithActiveSessionPost() throws ServletException, IOException {
        when(request.getSession(false)).thenReturn(session);

        servlet.doPost(request, response);

        verify(session).invalidate();
        verify(response).sendRedirect(CONTEXT_PATH + "/login.jsp");
    }

    @Test
    @DisplayName("Logout: When session is null, user is safely redirected to login.jsp without NullPointerException")
    void testLogoutWithoutSession() throws ServletException, IOException {
        when(request.getSession(false)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendRedirect(CONTEXT_PATH + "/login.jsp");
    }
}

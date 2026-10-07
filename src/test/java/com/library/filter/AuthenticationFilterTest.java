package com.library.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.mockito.Mockito.*;

public class AuthenticationFilterTest {

    private AuthenticationFilter filter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private FilterChain chain;

    private static final String CONTEXT_PATH = "/LibraryManagementSystem";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filter = new AuthenticationFilter();
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
    }

    @Test
    @DisplayName("Case 1a: Unauthenticated user accessing /admin/dashboard.jsp is redirected to login.jsp")
    void testUnauthenticatedUserAccessingAdminRoute() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/admin/dashboard.jsp");
        when(request.getSession(false)).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(response).sendRedirect(CONTEXT_PATH + "/login.jsp");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Case 1b: Unauthenticated user accessing /student/dashboard.jsp is redirected to login.jsp")
    void testUnauthenticatedUserAccessingStudentRoute() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/student/dashboard.jsp");
        when(request.getSession(false)).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(response).sendRedirect(CONTEXT_PATH + "/login.jsp");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Case 2: Logged-in ADMIN accessing /admin/dashboard.jsp is allowed")
    void testAdminAccessingAdminRoute() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/admin/dashboard.jsp");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("role")).thenReturn("ADMIN");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    @DisplayName("Case 3: Logged-in STUDENT accessing /student/dashboard.jsp is allowed")
    void testStudentAccessingStudentRoute() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/student/dashboard.jsp");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("role")).thenReturn("STUDENT");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    @DisplayName("Case 4: Logged-in STUDENT accessing /admin/dashboard.jsp is denied and redirected to student dashboard")
    void testStudentAccessingAdminRouteDenied() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/admin/dashboard.jsp");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("role")).thenReturn("STUDENT");

        filter.doFilter(request, response, chain);

        verify(response).sendRedirect(CONTEXT_PATH + "/student/dashboard.jsp");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Case 5: Logged-in ADMIN accessing /student/dashboard.jsp is redirected to admin dashboard")
    void testAdminAccessingStudentRouteRedirected() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/student/dashboard.jsp");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("role")).thenReturn("ADMIN");

        filter.doFilter(request, response, chain);

        verify(response).sendRedirect(CONTEXT_PATH + "/admin/dashboard.jsp");
        verify(chain, never()).doFilter(request, response);
    }
}

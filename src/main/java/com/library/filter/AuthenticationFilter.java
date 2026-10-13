package com.library.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filter to protect /admin/* and /student/* routes based on user authentication
 * and assigned roles (ADMIN or STUDENT).
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/admin/*", "/student/*"})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Filter initialization
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Prevent browser caching of protected resources
        httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setDateHeader("Expires", 0);

        // Retrieve current session without creating a new one
        HttpSession session = httpRequest.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String relativePath = requestURI.substring(contextPath.length());

        // 1. If user is not logged in, redirect to login.jsp
        if (session == null || role == null || role.trim().isEmpty()) {
            httpResponse.sendRedirect(contextPath + "/login.jsp");
            return;
        }

        // 2 & 4. Admin routes protection
        if (relativePath.startsWith("/admin/")) {
            if ("ADMIN".equalsIgnoreCase(role)) {
                // Rule 2: If the logged-in user is ADMIN, allow /admin/*
                chain.doFilter(request, response);
            } else {
                // Rule 4: If a STUDENT tries to access /admin/*, deny and redirect to student dashboard
                httpResponse.sendRedirect(contextPath + "/student/dashboard.jsp");
            }
            return;
        }

        // 3 & 5. Student routes protection
        if (relativePath.startsWith("/student/")) {
            if ("STUDENT".equalsIgnoreCase(role)) {
                // Rule 3: If the logged-in user is STUDENT, allow /student/*
                chain.doFilter(request, response);
            } else {
                // Rule 5: If an ADMIN tries to access student-only pages, redirect to admin dashboard
                httpResponse.sendRedirect(contextPath + "/admin/dashboard.jsp");
            }
            return;
        }

        // Default: Continue filter chain
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Filter cleanup
    }
}

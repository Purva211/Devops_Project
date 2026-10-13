package com.library.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout", "/LogoutServlet"})
public class LogoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processLogout(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processLogout(request, response);
    }

    /**
     * Handles logout by invalidating the current session and redirecting to login.jsp.
     */
    private void processLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // 1. Get the current HttpSession (if it exists)
        HttpSession session = request.getSession(false);

        // 2. Invalidate the session
        if (session != null) {
            session.invalidate();
        }

        // 3. Redirect the user to login.jsp
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
}

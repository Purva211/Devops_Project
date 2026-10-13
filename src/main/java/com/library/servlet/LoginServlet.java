package com.library.servlet;

import com.library.dao.UserDAO;
import com.library.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login", "/LoginServlet"})
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        if (this.userDAO == null) {
            this.userDAO = new UserDAO();
        }
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Forward GET requests to the login page
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Validate required fields
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("errorMessage", "Please provide both email and password.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        try {
            // Authenticate user via UserDAO
            User user = userDAO.loginUser(email.trim(), password);

            if (user != null) {
                // 1. Create / retrieve HttpSession
                HttpSession session = request.getSession(true);

                // 2. Store session attributes
                session.setAttribute("userId", user.getId());
                session.setAttribute("name", user.getName());
                session.setAttribute("email", user.getEmail());
                session.setAttribute("role", user.getRole());

                // 3. Role-based redirect
                String role = user.getRole();
                if ("ADMIN".equalsIgnoreCase(role)) {
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
                } else if ("STUDENT".equalsIgnoreCase(role)) {
                    response.sendRedirect(request.getContextPath() + "/student/dashboard.jsp");
                } else {
                    response.sendRedirect(request.getContextPath() + "/index.jsp");
                }
            } else {
                // Invalid credentials: Stay on login page and display error message
                request.setAttribute("errorMessage", "Invalid email or password. Please try again.");
                request.setAttribute("email", email);
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }

        } catch (Exception e) {
            System.err.println("Unexpected error during login: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred while processing your login. Please try again.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}

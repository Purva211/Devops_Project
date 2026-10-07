package com.library.servlet;

import com.library.dao.UserDAO;
import com.library.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.regex.Pattern;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register", "/RegisterServlet"})
public class RegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

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
        // Forward GET requests to the registration page
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // Trim inputs where appropriate
        if (name != null) name = name.trim();
        if (email != null) email = email.trim();

        // 1. Validate required fields
        if (name == null || name.isEmpty() ||
            email == null || email.isEmpty() ||
            password == null || password.isEmpty() ||
            confirmPassword == null || confirmPassword.isEmpty()) {

            forwardWithError(request, response, "All fields are required.", name, email);
            return;
        }

        // 2. Validate email format
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            forwardWithError(request, response, "Please enter a valid email address.", name, email);
            return;
        }

        // 3. Validate password length
        if (password.length() < 6) {
            forwardWithError(request, response, "Password must be at least 6 characters long.", name, email);
            return;
        }

        // 4. Validate password confirmation
        if (!password.equals(confirmPassword)) {
            forwardWithError(request, response, "Passwords do not match.", name, email);
            return;
        }

        try {
            // 5. Check if email already exists
            if (userDAO.isEmailRegistered(email)) {
                forwardWithError(request, response, "An account with this email already exists. Please login instead.", name, email);
                return;
            }

            // 6 & 7. Create User object with role = STUDENT
            User newUser = new User(name, email, password, "STUDENT");

            // 8. Save user to database
            boolean isSaved = userDAO.registerUser(newUser);

            if (isSaved) {
                // 9. On success, redirect to login.jsp with success message
                String message = URLEncoder.encode("Registration successful! Please log in with your credentials.", "UTF-8");
                response.sendRedirect("login.jsp?success=" + message);
            } else {
                // 10. On failure, show safe user-friendly error message
                forwardWithError(request, response, "Registration failed due to a system error. Please try again.", name, email);
            }

        } catch (Exception e) {
            // Log internally without exposing stack trace / DB details to the client
            System.err.println("Unexpected error during user registration: " + e.getMessage());
            e.printStackTrace();
            forwardWithError(request, response, "An unexpected error occurred. Please try again later.", name, email);
        }
    }

    /**
     * Helper method to preserve input and forward with an error message.
     */
    private void forwardWithError(HttpServletRequest request, HttpServletResponse response,
                                  String errorMessage, String name, String email)
            throws ServletException, IOException {
        request.setAttribute("errorMessage", errorMessage);
        request.setAttribute("name", name);
        request.setAttribute("email", email);
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }
}

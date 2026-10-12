package com.library.servlet;

import com.library.dao.BookDAO;
import com.library.model.Book;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "AddBookServlet", urlPatterns = {"/admin/add-book", "/admin/AddBookServlet"})
public class AddBookServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private BookDAO bookDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        if (this.bookDAO == null) {
            this.bookDAO = new BookDAO();
        }
    }

    public void setBookDAO(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Session and role verification
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;

        if (session == null || role == null || !"ADMIN".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Forward GET requests to the add-book JSP page
        request.getRequestDispatcher("/admin/add-book.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // 1. Session and role verification
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;

        if (session == null || role == null || !"ADMIN".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // 2. Read request parameters
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String category = request.getParameter("category");
        String quantityStr = request.getParameter("quantity");

        if (title != null) title = title.trim();
        if (author != null) author = author.trim();
        if (category != null) category = category.trim();
        if (quantityStr != null) quantityStr = quantityStr.trim();

        // 3. Validation
        if (title == null || title.isEmpty()) {
            forwardWithError(request, response, "Book title is required.", title, author, category, quantityStr);
            return;
        }

        if (author == null || author.isEmpty()) {
            forwardWithError(request, response, "Author name is required.", title, author, category, quantityStr);
            return;
        }

        int quantity;
        try {
            if (quantityStr == null || quantityStr.isEmpty()) {
                forwardWithError(request, response, "Quantity is required.", title, author, category, quantityStr);
                return;
            }
            quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                forwardWithError(request, response, "Quantity must be greater than 0.", title, author, category, quantityStr);
                return;
            }
        } catch (NumberFormatException e) {
            forwardWithError(request, response, "Quantity must be a valid integer number.", title, author, category, quantityStr);
            return;
        }

        try {
            // Category can be empty/optional
            String cleanCategory = (category != null) ? category : "";

            // 4. Create Book object
            Book book = new Book(title, author, cleanCategory, quantity, quantity);

            // 5. Save book to database via BookDAO
            boolean isSaved = bookDAO.addBook(book);

            if (isSaved) {
                // 6. On success, redirect to admin books page
                response.sendRedirect(request.getContextPath() + "/admin/search-books");
            } else {
                // 7. On failure, return to add-book page with error message
                forwardWithError(request, response, "Failed to add book due to a database error. Please try again.", title, author, category, quantityStr);
            }

        } catch (Exception e) {
            System.err.println("Unexpected error while adding book: " + e.getMessage());
            e.printStackTrace();
            forwardWithError(request, response, "An unexpected error occurred while processing your request. Please try again.", title, author, category, quantityStr);
        }
    }

    /**
     * Helper method to preserve inputs and forward to the add-book page with an error message.
     */
    private void forwardWithError(HttpServletRequest request, HttpServletResponse response,
                                  String errorMessage, String title, String author, String category, String quantity)
            throws ServletException, IOException {
        request.setAttribute("errorMessage", errorMessage);
        request.setAttribute("title", title);
        request.setAttribute("author", author);
        request.setAttribute("category", category);
        request.setAttribute("quantity", quantity);
        request.getRequestDispatcher("/admin/add-book.jsp").forward(request, response);
    }
}

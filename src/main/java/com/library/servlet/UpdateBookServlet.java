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

@WebServlet(name = "UpdateBookServlet", urlPatterns = {"/admin/update-book", "/admin/UpdateBookServlet"})
public class UpdateBookServlet extends HttpServlet {

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

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 1. Session and role verification
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;

        if (session == null || role == null || !"ADMIN".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // 2. Read and validate book id
        String idStr = request.getParameter("id");
        int id;
        try {
            if (idStr == null || idStr.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/admin/search-books");
                return;
            }
            id = Integer.parseInt(idStr.trim());
            if (id <= 0) {
                response.sendRedirect(request.getContextPath() + "/admin/search-books");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/search-books");
            return;
        }

        try {
            // 3. Retrieve book from database
            Book book = bookDAO.getBookById(id);

            if (book == null) {
                response.sendRedirect(request.getContextPath() + "/admin/search-books");
                return;
            }

            // 4. Store book in request attribute and forward to update-book.jsp
            request.setAttribute("book", book);
            request.getRequestDispatcher("/admin/update-book.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("Unexpected error retrieving book for update: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/search-books");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 1. Session and role verification
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;

        if (session == null || role == null || !"ADMIN".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // 2. Read request parameters
        String idStr = request.getParameter("id");
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String category = request.getParameter("category");
        String quantityStr = request.getParameter("quantity");

        if (title != null) title = title.trim();
        if (author != null) author = author.trim();
        if (category != null) category = category.trim();
        if (quantityStr != null) quantityStr = quantityStr.trim();

        // 3. Validate ID
        int id;
        try {
            if (idStr == null || idStr.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/admin/search-books");
                return;
            }
            id = Integer.parseInt(idStr.trim());
            if (id <= 0) {
                response.sendRedirect(request.getContextPath() + "/admin/search-books");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/search-books");
            return;
        }

        // Fetch existing book to preserve available_quantity
        Book existingBook = bookDAO.getBookById(id);
        if (existingBook == null) {
            response.sendRedirect(request.getContextPath() + "/admin/search-books");
            return;
        }

        int availableQuantity = existingBook.getAvailableQuantity();
        String cleanCategory = (category != null) ? category : "";

        // 4. Validate other fields
        if (title == null || title.isEmpty()) {
            Book tempBook = new Book(id, (title != null ? title : ""), (author != null ? author : ""), cleanCategory, 0, availableQuantity);
            forwardWithError(request, response, "Book title is required.", tempBook);
            return;
        }

        if (author == null || author.isEmpty()) {
            Book tempBook = new Book(id, title, (author != null ? author : ""), cleanCategory, 0, availableQuantity);
            forwardWithError(request, response, "Author name is required.", tempBook);
            return;
        }

        int quantity;
        try {
            if (quantityStr == null || quantityStr.isEmpty()) {
                Book tempBook = new Book(id, title, author, cleanCategory, 0, availableQuantity);
                forwardWithError(request, response, "Quantity is required.", tempBook);
                return;
            }
            quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                Book tempBook = new Book(id, title, author, cleanCategory, quantity, availableQuantity);
                forwardWithError(request, response, "Quantity must be greater than 0.", tempBook);
                return;
            }
        } catch (NumberFormatException e) {
            Book tempBook = new Book(id, title, author, cleanCategory, 0, availableQuantity);
            forwardWithError(request, response, "Quantity must be a valid integer number.", tempBook);
            return;
        }

        try {
            // 5. Update book preserving available_quantity
            Book updatedBook = new Book(id, title, author, cleanCategory, quantity, availableQuantity);

            boolean isUpdated = bookDAO.updateBook(updatedBook);

            if (isUpdated) {
                // 6. On success, redirect to admin books list
                response.sendRedirect(request.getContextPath() + "/admin/search-books");
            } else {
                // 7. On failure, return to update form with error message
                forwardWithError(request, response, "Failed to update book due to a database error. Please try again.", updatedBook);
            }

        } catch (Exception e) {
            System.err.println("Unexpected error while updating book: " + e.getMessage());
            e.printStackTrace();
            Book fallbackBook = new Book(id, title, author, cleanCategory, quantity, availableQuantity);
            forwardWithError(request, response, "An unexpected error occurred while processing the update. Please try again.", fallbackBook);
        }
    }

    /**
     * Helper method to preserve book input and forward to update-book.jsp with an error message.
     */
    private void forwardWithError(HttpServletRequest request, HttpServletResponse response,
                                  String errorMessage, Book book)
            throws ServletException, IOException {
        request.setAttribute("errorMessage", errorMessage);
        request.setAttribute("book", book);
        request.getRequestDispatcher("/admin/update-book.jsp").forward(request, response);
    }
}

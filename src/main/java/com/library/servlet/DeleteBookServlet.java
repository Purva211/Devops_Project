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

@WebServlet(name = "DeleteBookServlet", urlPatterns = {"/admin/delete-book", "/admin/DeleteBookServlet"})
public class DeleteBookServlet extends HttpServlet {

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
        // Redirect GET requests safely back to the book management search page
        response.sendRedirect(request.getContextPath() + "/admin/search-books");
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
            // 3. Verify book exists before deleting
            Book book = bookDAO.getBookById(id);
            if (book == null) {
                response.sendRedirect(request.getContextPath() + "/admin/search-books");
                return;
            }

            // 4. Delete the book
            boolean isDeleted = bookDAO.deleteBook(id);
            if (isDeleted) {
                response.sendRedirect(request.getContextPath() + "/admin/search-books");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/search-books?error=Failed+to+delete+book");
            }

        } catch (Exception e) {
            System.err.println("Unexpected error while deleting book with id " + id + ": " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/search-books?error=An+unexpected+error+occurred");
        }
    }
}

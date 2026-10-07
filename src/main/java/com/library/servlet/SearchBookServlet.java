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
import java.util.Collections;
import java.util.List;

@WebServlet(name = "SearchBookServlet", urlPatterns = {"/admin/search-books", "/admin/SearchBookServlet"})
public class SearchBookServlet extends HttpServlet {

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

        // 1. Session and role verification
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;

        if (session == null || role == null || !"ADMIN".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // 2. Read search keyword parameter
        String keyword = request.getParameter("keyword");
        String trimmedKeyword = (keyword != null) ? keyword.trim() : null;

        List<Book> books;
        try {
            // 3. Search books based on keyword presence
            if (trimmedKeyword == null || trimmedKeyword.isEmpty()) {
                books = bookDAO.getAllBooks();
            } else {
                books = bookDAO.searchBooks(trimmedKeyword);
            }
        } catch (Exception e) {
            System.err.println("Unexpected error while searching books: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred while retrieving books.");
            books = Collections.emptyList();
        }

        // 4. Set request attributes
        request.setAttribute("books", books);
        request.setAttribute("searchKeyword", (trimmedKeyword != null ? trimmedKeyword : ""));

        // 5. Forward to books JSP page
        request.getRequestDispatcher("/admin/books.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}

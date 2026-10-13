package com.library.servlet;

import com.library.dao.IssueDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "IssueBookServlet", urlPatterns = {"/student/issue-book", "/student/IssueBookServlet"})
public class IssueBookServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private IssueDAO issueDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        if (this.issueDAO == null) {
            this.issueDAO = new IssueDAO();
        }
    }

    public void setIssueDAO(IssueDAO issueDAO) {
        this.issueDAO = issueDAO;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/student/books.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 1. Session and role check
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;
        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;

        if (session == null || role == null || !"STUDENT".equalsIgnoreCase(role) || userId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // 2. Validate book ID parameter
        String bookIdStr = request.getParameter("book_id");
        int bookId;
        try {
            if (bookIdStr == null || bookIdStr.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/student/books.jsp?error=Invalid+book+selection.");
                return;
            }
            bookId = Integer.parseInt(bookIdStr.trim());
            if (bookId <= 0) {
                response.sendRedirect(request.getContextPath() + "/student/books.jsp?error=Invalid+book+ID.");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/student/books.jsp?error=Invalid+book+ID+format.");
            return;
        }

        try {
            // 3. Issue the book via IssueDAO
            boolean isIssued = issueDAO.issueBook(userId, bookId);

            if (isIssued) {
                // Redirect to My Issued Books page on success
                response.sendRedirect(request.getContextPath() + "/student/issued-books.jsp?success=Book+issued+successfully!");
            } else {
                // Redirect back to books page with informative error message
                response.sendRedirect(request.getContextPath() + "/student/books.jsp?error=Unable+to+issue+book.+It+may+be+out+of+stock+or+already+issued+to+you.");
            }
        } catch (Exception e) {
            System.err.println("Unexpected error while issuing book: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/student/books.jsp?error=An+unexpected+error+occurred.+Please+try+again.");
        }
    }
}

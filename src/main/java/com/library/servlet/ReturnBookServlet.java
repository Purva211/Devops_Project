package com.library.servlet;

import com.library.dao.IssueDAO;
import com.library.model.Issue;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "ReturnBookServlet", urlPatterns = {"/student/return-book", "/admin/return-book", "/student/ReturnBookServlet", "/admin/ReturnBookServlet"})
public class ReturnBookServlet extends HttpServlet {

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
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;

        if ("ADMIN".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/admin/issued-books.jsp");
        } else {
            response.sendRedirect(request.getContextPath() + "/student/issued-books.jsp");
        }
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

        if (session == null || role == null || userId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);
        String returnRedirectUrl = isAdmin
                ? request.getContextPath() + "/admin/issued-books.jsp"
                : request.getContextPath() + "/student/issued-books.jsp";

        // 2. Validate issue ID parameter
        String issueIdStr = request.getParameter("issue_id");
        int issueId;
        try {
            if (issueIdStr == null || issueIdStr.trim().isEmpty()) {
                response.sendRedirect(returnRedirectUrl + "?error=Invalid+issue+record.");
                return;
            }
            issueId = Integer.parseInt(issueIdStr.trim());
            if (issueId <= 0) {
                response.sendRedirect(returnRedirectUrl + "?error=Invalid+issue+ID.");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(returnRedirectUrl + "?error=Invalid+issue+ID+format.");
            return;
        }

        try {
            // 3. Security check: if student, verify that the issue belongs to this student
            if (!isAdmin) {
                Issue existingIssue = issueDAO.getIssueById(issueId);
                if (existingIssue == null || existingIssue.getUserId() != userId) {
                    response.sendRedirect(returnRedirectUrl + "?error=You+are+not+authorized+to+return+this+book.");
                    return;
                }
            }

            // 4. Return book via IssueDAO
            boolean isReturned = issueDAO.returnBook(issueId);

            if (isReturned) {
                response.sendRedirect(returnRedirectUrl + "?success=Book+returned+successfully!");
            } else {
                response.sendRedirect(returnRedirectUrl + "?error=Unable+to+return+book.+It+may+have+already+been+returned.");
            }
        } catch (Exception e) {
            System.err.println("Unexpected error while returning book: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(returnRedirectUrl + "?error=An+unexpected+error+occurred.+Please+try+again.");
        }
    }
}

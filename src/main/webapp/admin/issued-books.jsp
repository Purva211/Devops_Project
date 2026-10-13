<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.library.dao.IssueDAO" %>
<%@ page import="com.library.model.Issue" %>
<%
    // Prevent browser caching of protected pages
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    // Read user information from HttpSession
    String name = (session != null) ? (String) session.getAttribute("name") : null;
    String role = (session != null) ? (String) session.getAttribute("role") : null;

    // Safety check: redirect unauthenticated or non-admin users
    if (session == null || role == null || !"ADMIN".equalsIgnoreCase(role)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    IssueDAO issueDAO = new IssueDAO();
    List<Issue> issues = issueDAO.getAllIssues();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>All Issued Books - Library Management System</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Inter', sans-serif;
            background-color: #f8fafc;
            min-height: 100vh;
        }
        .navbar-custom {
            background-color: #0f172a;
        }
        .main-card {
            background: #ffffff;
            border-radius: 12px;
            border: 1px solid #e2e8f0;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
            padding: 2rem;
            margin: 2rem auto;
        }
        .table th {
            font-weight: 600;
            color: #334155;
            background-color: #f1f5f9;
        }
    </style>
</head>
<body>
    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg navbar-dark navbar-custom px-4 py-3">
        <div class="container-fluid">
            <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/admin/dashboard.jsp">Library Management System</a>
            <div class="ms-auto d-flex align-items-center gap-3">
                <span class="text-light small d-none d-md-inline">Logged in as: <strong><%= (name != null ? name : "Admin") %></strong></span>
                <a href="${pageContext.request.contextPath}/logout" id="navLogoutBtn" class="btn btn-outline-light btn-sm">Logout</a>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="container-fluid px-4 px-lg-5">
        <div class="main-card">
            <!-- Header Section -->
            <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3 mb-4 pb-3 border-bottom">
                <div>
                    <div class="d-flex align-items-center gap-2 mb-1">
                        <h2 class="fw-bold text-dark mb-0">Issued Books Overview</h2>
                        <span class="badge bg-danger">ADMIN</span>
                    </div>
                    <p class="text-secondary mb-0">Complete transaction history of all issued and returned library books</p>
                </div>
                <div class="d-flex gap-2">
                    <a href="${pageContext.request.contextPath}/admin/dashboard.jsp" class="btn btn-outline-secondary">
                        &larr; Dashboard
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/search-books" class="btn btn-outline-primary">
                        Manage Books Catalog
                    </a>
                </div>
            </div>

            <!-- Alerts -->
            <% 
                String errorMessage = request.getParameter("error");
                if (errorMessage != null && !errorMessage.trim().isEmpty()) { 
            %>
                <div class="alert alert-danger alert-dismissible fade show py-2 px-3 mb-4" role="alert">
                    <small><%= errorMessage %></small>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close" style="padding: 0.75rem;"></button>
                </div>
            <% } %>

            <% 
                String successMessage = request.getParameter("success");
                if (successMessage != null && !successMessage.trim().isEmpty()) { 
            %>
                <div class="alert alert-success alert-dismissible fade show py-2 px-3 mb-4" role="alert">
                    <small><%= successMessage %></small>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close" style="padding: 0.75rem;"></button>
                </div>
            <% } %>

            <!-- Issues Table -->
            <% if (issues != null && !issues.isEmpty()) { %>
                <div class="table-responsive">
                    <table class="table table-hover table-bordered align-middle">
                        <thead>
                            <tr>
                                <th style="width: 70px;" class="text-center">ID</th>
                                <th>Student Name</th>
                                <th>Student Email</th>
                                <th>Book Title</th>
                                <th>Author</th>
                                <th style="width: 120px;" class="text-center">Issue Date</th>
                                <th style="width: 120px;" class="text-center">Return Date</th>
                                <th style="width: 110px;" class="text-center">Status</th>
                                <th style="width: 150px;" class="text-center">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Issue issue : issues) { 
                                boolean isCurrentlyIssued = "ISSUED".equalsIgnoreCase(issue.getStatus());
                            %>
                                <tr>
                                    <td class="text-center fw-semibold text-muted">#<%= issue.getId() %></td>
                                    <td class="fw-semibold text-dark"><%= (issue.getUserName() != null ? issue.getUserName() : "User #" + issue.getUserId()) %></td>
                                    <td class="text-muted small"><%= (issue.getUserEmail() != null ? issue.getUserEmail() : "N/A") %></td>
                                    <td class="fw-medium text-dark"><%= issue.getBookTitle() %></td>
                                    <td><%= issue.getBookAuthor() %></td>
                                    <td class="text-center"><%= issue.getIssueDate() %></td>
                                    <td class="text-center">
                                        <% if (issue.getReturnDate() != null) { %>
                                            <%= issue.getReturnDate() %>
                                        <% } else { %>
                                            <span class="text-muted small">&mdash;</span>
                                        <% } %>
                                    </td>
                                    <td class="text-center">
                                        <% if (isCurrentlyIssued) { %>
                                            <span class="badge bg-primary px-3 py-2">ISSUED</span>
                                        <% } else { %>
                                            <span class="badge bg-success px-3 py-2">RETURNED</span>
                                        <% } %>
                                    </td>
                                    <td class="text-center">
                                        <% if (isCurrentlyIssued) { %>
                                            <form method="POST" action="${pageContext.request.contextPath}/admin/return-book" class="d-inline"
                                                  onsubmit="return confirm('Mark this book as returned?');">
                                                <input type="hidden" name="issue_id" value="<%= issue.getId() %>">
                                                <button type="submit" class="btn btn-sm btn-outline-success px-2">
                                                    Mark Returned
                                                </button>
                                            </form>
                                        <% } else { %>
                                            <span class="text-muted small">Returned</span>
                                        <% } %>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            <% } else { %>
                <div class="text-center py-5 border rounded-3 bg-light">
                    <p class="text-muted fs-5 mb-0">No library transactions recorded yet.</p>
                </div>
            <% } %>
        </div>
    </div>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

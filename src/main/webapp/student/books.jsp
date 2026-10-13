<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="com.library.dao.BookDAO" %>
<%@ page import="com.library.dao.IssueDAO" %>
<%@ page import="com.library.model.Book" %>
<%
    // Prevent browser caching of protected pages
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    // Read user information from HttpSession
    String name = (session != null) ? (String) session.getAttribute("name") : null;
    String role = (session != null) ? (String) session.getAttribute("role") : null;
    Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;

    // Safety check: redirect unauthenticated or non-student users
    if (session == null || role == null || !"STUDENT".equalsIgnoreCase(role) || userId == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    // Retrieve search keyword if provided
    String searchKeyword = request.getParameter("keyword");
    String trimmedKeyword = (searchKeyword != null) ? searchKeyword.trim() : "";

    BookDAO bookDAO = new BookDAO();
    IssueDAO issueDAO = new IssueDAO();

    List<Book> books = (!trimmedKeyword.isEmpty()) 
            ? bookDAO.searchBooks(trimmedKeyword) 
            : bookDAO.getAllBooks();

    // Check which books the student currently has actively issued
    Set<Integer> activeIssuedBookIds = issueDAO.getActiveIssuedBookIds(userId);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Available Books - Library Management System</title>
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
            background-color: #1e3a8a;
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
            <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/student/dashboard.jsp">Library Management System</a>
            <div class="ms-auto d-flex align-items-center gap-3">
                <span class="text-light small d-none d-md-inline">Student: <strong><%= (name != null ? name : "Student") %></strong></span>
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
                        <h2 class="fw-bold text-dark mb-0">Available Books Catalog</h2>
                        <span class="badge bg-success">STUDENT</span>
                    </div>
                    <p class="text-secondary mb-0">Browse library books and issue available copies</p>
                </div>
                <div class="d-flex gap-2">
                    <a href="${pageContext.request.contextPath}/student/dashboard.jsp" class="btn btn-outline-secondary">
                        &larr; Dashboard
                    </a>
                    <a href="${pageContext.request.contextPath}/student/issued-books.jsp" class="btn btn-outline-primary">
                        My Issued Books &rarr;
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

            <!-- Search Form -->
            <div class="card bg-light border-0 mb-4 p-3 rounded-3">
                <form action="${pageContext.request.contextPath}/student/books.jsp" method="GET" class="row g-2 align-items-center">
                    <div class="col-12 col-md-6 col-lg-7">
                        <div class="input-group">
                            <input type="text" class="form-control" name="keyword" id="searchKeywordInput"
                                   placeholder="Search by title or author..."
                                   value="<%= trimmedKeyword %>">
                            <button type="submit" id="searchBtn" class="btn btn-primary">
                                Search
                            </button>
                        </div>
                    </div>
                    <div class="col-12 col-md-6 col-lg-5 d-flex gap-2">
                        <a href="${pageContext.request.contextPath}/student/books.jsp" id="viewAllBtn" class="btn btn-outline-secondary">
                            View All
                        </a>
                        <% if (!trimmedKeyword.isEmpty()) { %>
                            <span class="align-self-center text-muted small ms-2">
                                Showing results for: <strong><%= trimmedKeyword %></strong>
                            </span>
                        <% } %>
                    </div>
                </form>
            </div>

            <!-- Books Table -->
            <% if (books != null && !books.isEmpty()) { %>
                <div class="table-responsive">
                    <table class="table table-hover table-bordered align-middle">
                        <thead>
                            <tr>
                                <th style="width: 70px;">ID</th>
                                <th>Title</th>
                                <th>Author</th>
                                <th>Category</th>
                                <th style="width: 150px;" class="text-center">Availability</th>
                                <th style="width: 170px;" class="text-center">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Book book : books) { 
                                boolean isAlreadyIssued = activeIssuedBookIds.contains(book.getId());
                                boolean isAvailable = book.getAvailableQuantity() > 0;
                            %>
                                <tr>
                                    <td class="fw-semibold text-muted"><%= book.getId() %></td>
                                    <td class="fw-medium text-dark"><%= book.getTitle() %></td>
                                    <td><%= book.getAuthor() %></td>
                                    <td>
                                        <% if (book.getCategory() != null && !book.getCategory().trim().isEmpty()) { %>
                                            <span class="badge bg-light text-dark border"><%= book.getCategory() %></span>
                                        <% } else { %>
                                            <span class="text-muted small">N/A</span>
                                        <% } %>
                                    </td>
                                    <td class="text-center">
                                        <% if (book.getAvailableQuantity() > 0) { %>
                                            <span class="badge bg-success"><%= book.getAvailableQuantity() %> Available</span>
                                        <% } else { %>
                                            <span class="badge bg-danger">0 (Out of Stock)</span>
                                        <% } %>
                                    </td>
                                    <td class="text-center">
                                        <% if (isAlreadyIssued) { %>
                                            <span class="badge bg-warning text-dark py-2 px-3">Already Issued</span>
                                        <% } else if (isAvailable) { %>
                                            <form method="POST" action="${pageContext.request.contextPath}/student/issue-book" class="d-inline"
                                                  onsubmit="return confirm('Do you want to issue this book?');">
                                                <input type="hidden" name="book_id" value="<%= book.getId() %>">
                                                <button type="submit" class="btn btn-sm btn-primary px-3">
                                                    Issue Book
                                                </button>
                                            </form>
                                        <% } else { %>
                                            <button class="btn btn-sm btn-secondary px-3" disabled>
                                                Out of Stock
                                            </button>
                                        <% } %>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            <% } else { %>
                <div class="text-center py-5 border rounded-3 bg-light">
                    <p class="text-muted fs-5 mb-0">No books found in the library catalog.</p>
                </div>
            <% } %>
        </div>
    </div>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

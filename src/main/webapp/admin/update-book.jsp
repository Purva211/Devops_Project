<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.library.model.Book" %>
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

    Book book = (Book) request.getAttribute("book");
    if (book == null) {
        response.sendRedirect(request.getContextPath() + "/admin/search-books");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Update Book - Library Management System</title>
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
        .dashboard-card {
            background: #ffffff;
            border-radius: 12px;
            border: 1px solid #e2e8f0;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
            padding: 2rem;
            max-width: 650px;
            margin: 3rem auto;
        }
        .form-label {
            font-weight: 600;
            font-size: 0.875rem;
            color: #334155;
            margin-bottom: 0.4rem;
        }
        .form-control {
            border-radius: 8px;
            padding: 0.65rem 0.9rem;
            border: 1px solid #cbd5e1;
            font-size: 0.95rem;
        }
        .form-control:focus {
            border-color: #2563eb;
            box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.15);
        }
        .readonly-info {
            background-color: #f1f5f9;
            cursor: not-allowed;
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
    <div class="container">
        <div class="dashboard-card">
            <!-- Header -->
            <div class="d-flex justify-content-between align-items-start mb-4 border-bottom pb-3">
                <div>
                    <h2 class="fw-bold text-dark mb-1">Update Book</h2>
                    <p class="text-secondary mb-0">Modify book details in library catalog (ID: #<%= book.getId() %>)</p>
                </div>
                <span class="badge bg-danger px-3 py-2 fs-6">ADMIN</span>
            </div>

            <!-- Error Message Display -->
            <% 
                String errorMessage = (String) request.getAttribute("errorMessage");
                if (errorMessage != null && !errorMessage.trim().isEmpty()) { 
            %>
                <div class="alert alert-danger alert-dismissible fade show py-2 px-3 mb-4" role="alert">
                    <small><%= errorMessage %></small>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close" style="padding: 0.75rem;"></button>
                </div>
            <% } %>

            <form id="updateBookForm" action="${pageContext.request.contextPath}/admin/update-book" method="POST">
                <!-- Hidden Book ID -->
                <input type="hidden" name="id" value="<%= book.getId() %>">

                <!-- Title -->
                <div class="mb-3">
                    <label for="title" class="form-label">Book Title <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="title" name="title"
                           value="<%= (book.getTitle() != null ? book.getTitle() : "") %>"
                           placeholder="Enter book title" required autofocus>
                </div>

                <!-- Author -->
                <div class="mb-3">
                    <label for="author" class="form-label">Author <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="author" name="author"
                           value="<%= (book.getAuthor() != null ? book.getAuthor() : "") %>"
                           placeholder="Enter author name" required>
                </div>

                <!-- Category -->
                <div class="mb-3">
                    <label for="category" class="form-label">Category <span class="text-muted small">(Optional)</span></label>
                    <input type="text" class="form-control" id="category" name="category"
                           value="<%= (book.getCategory() != null ? book.getCategory() : "") %>"
                           placeholder="e.g. Programming, Science, Fiction">
                </div>

                <!-- Total Quantity -->
                <div class="mb-3">
                    <label for="quantity" class="form-label">Total Quantity <span class="text-danger">*</span></label>
                    <input type="number" class="form-control" id="quantity" name="quantity"
                           value="<%= (book.getQuantity() > 0 ? book.getQuantity() : "") %>"
                           min="1" required>
                </div>

                <!-- Read-Only Available Quantity -->
                <div class="mb-4">
                    <label class="form-label">Current Available Quantity <span class="text-muted small">(Read-only)</span></label>
                    <input type="text" class="form-control readonly-info"
                           value="<%= book.getAvailableQuantity() %>" readonly disabled>
                    <div class="form-text text-muted">
                        Available quantity is preserved and cannot be edited directly here.
                    </div>
                </div>

                <!-- Action Buttons -->
                <div class="d-flex justify-content-between align-items-center pt-2">
                    <a href="${pageContext.request.contextPath}/admin/search-books" id="cancelBtn" class="btn btn-outline-secondary">
                        &larr; Cancel / Back to Books
                    </a>
                    <button type="submit" id="updateBookSubmitBtn" class="btn btn-primary px-4">
                        Update Book
                    </button>
                </div>
            </form>
        </div>
    </div>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

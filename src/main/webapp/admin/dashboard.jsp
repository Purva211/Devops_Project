<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - Library Management System</title>
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
            padding: 2.5rem 2rem;
            max-width: 500px;
            margin: 4rem auto;
            text-align: center;
        }
    </style>
</head>
<body>
    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg navbar-dark navbar-custom px-4 py-3">
        <div class="container-fluid">
            <a class="navbar-brand fw-bold" href="#">Library Management System</a>
            <div class="ms-auto">
                <a href="${pageContext.request.contextPath}/logout" id="navLogoutBtn" class="btn btn-outline-light btn-sm">Logout</a>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="container">
        <div class="dashboard-card">
            <h2 class="fw-bold text-dark mb-2">Admin Dashboard</h2>
            <p class="text-secondary fs-5 mb-4">Welcome, <%= (name != null ? name : "Admin User") %></p>

            <div class="border-top pt-4">
                <h4 class="fw-semibold text-dark mb-3">Book Management</h4>
                <div class="d-grid gap-2 col-10 mx-auto">
                    <a href="${pageContext.request.contextPath}/admin/add-book.jsp" id="addBookBtn" class="btn btn-primary py-2">
                        Add Book
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/search-books" id="viewBooksBtn" class="btn btn-outline-primary py-2">
                        View / Manage Books
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/issued-books.jsp" id="viewIssuedBooksBtn" class="btn btn-outline-dark py-2">
                        Issued Books History
                    </a>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Prevent browser caching of protected pages
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    // Read user information from HttpSession
    String name = (session != null) ? (String) session.getAttribute("name") : null;
    String role = (session != null) ? (String) session.getAttribute("role") : null;
    String email = (session != null) ? (String) session.getAttribute("email") : null;

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
    </style>
</head>
<body>
    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg navbar-dark navbar-custom px-4 py-3">
        <div class="container-fluid">
            <a class="navbar-brand fw-bold" href="#">Library Management System</a>
            <div class="ms-auto d-flex align-items-center gap-3">
                <a href="${pageContext.request.contextPath}/logout" id="navLogoutBtn" class="btn btn-outline-light btn-sm">Logout</a>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="container">
        <div class="dashboard-card">
            <div class="d-flex justify-content-between align-items-start mb-4 border-bottom pb-3">
                <div>
                    <h2 class="fw-bold text-dark mb-1">Admin Dashboard</h2>
                    <p class="text-secondary mb-0">Administrator Portal & Control Panel</p>
                </div>
                <span class="badge bg-danger px-3 py-2 fs-6">ADMIN</span>
            </div>

            <div class="mb-4">
                <h4 class="text-primary fw-semibold" id="welcomeMessage">Welcome, <%= (name != null ? name : "Admin") %></h4>
                <p class="fs-5 mt-2"><strong>Role:</strong> <span class="badge bg-secondary">ADMIN</span></p>
                <% if (email != null) { %>
                    <p class="text-muted"><strong>Email:</strong> <%= email %></p>
                <% } %>
            </div>

            <div class="alert alert-info py-2 px-3 mb-4" role="alert">
                <small>Authentication successful. This is a placeholder dashboard for role verification.</small>
            </div>

            <div class="d-flex gap-2">
                <a href="${pageContext.request.contextPath}/logout" id="logoutBtn" class="btn btn-danger">Logout</a>
            </div>
        </div>
    </div>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

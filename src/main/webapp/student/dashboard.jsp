<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.library.dao.IssueDAO" %>
<%@ page import="com.library.model.Issue" %>
<%@ page import="java.util.List" %>
<%
    // Prevent browser caching of protected pages
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    // Read user information from HttpSession
    String name = (session != null) ? (String) session.getAttribute("name") : null;
    String role = (session != null) ? (String) session.getAttribute("role") : null;
    String email = (session != null) ? (String) session.getAttribute("email") : null;
    Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;

    // Safety check: redirect unauthenticated or non-student users
    if (session == null || role == null || !"STUDENT".equalsIgnoreCase(role) || userId == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    IssueDAO issueDAO = new IssueDAO();
    List<Issue> myIssues = issueDAO.getIssuesByUserId(userId);
    int activeIssuesCount = 0;
    for (Issue issue : myIssues) {
        if ("ISSUED".equalsIgnoreCase(issue.getStatus())) {
            activeIssuesCount++;
        }
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Dashboard - Library Management System</title>
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
        .card-custom {
            background: #ffffff;
            border-radius: 12px;
            border: 1px solid #e2e8f0;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }
        .card-custom:hover {
            transform: translateY(-3px);
            box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
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
    <div class="container py-5">
        <div class="row justify-content-center">
            <div class="col-12 col-lg-10">
                <!-- Welcome Banner -->
                <div class="card bg-white border-0 shadow-sm p-4 rounded-4 mb-4">
                    <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3">
                        <div>
                            <div class="d-flex align-items-center gap-2 mb-1">
                                <h2 class="fw-bold text-dark mb-0">Student Dashboard</h2>
                                <span class="badge bg-success">STUDENT</span>
                            </div>
                            <p class="text-secondary mb-0">Welcome back, <strong><%= (name != null ? name : "Student") %></strong> (<%= (email != null ? email : "") %>)</p>
                        </div>
                        <div>
                            <span class="badge bg-primary fs-6 px-3 py-2">
                                Currently Borrowed: <%= activeIssuesCount %> <%= activeIssuesCount == 1 ? "Book" : "Books" %>
                            </span>
                        </div>
                    </div>
                </div>

                <!-- Navigation Cards -->
                <div class="row g-4">
                    <!-- Available Books Card -->
                    <div class="col-12 col-md-6">
                        <div class="card card-custom h-100 p-4">
                            <div class="d-flex align-items-center mb-3">
                                <div class="bg-primary bg-opacity-10 text-primary p-3 rounded-circle me-3">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" fill="currentColor" class="bi bi-book" viewBox="0 0 16 16">
                                        <path d="M1 2.828c.885-.37 2.154-.769 3.388-.893 1.33-.134 2.458.063 3.112.752v9.746c-.935-.53-2.12-.603-3.213-.493-1.18.12-2.37.461-3.287.811zm7.5-.141c.654-.689 1.782-.886 3.112-.752 1.234.124 2.503.523 3.388.893v9.923c-.918-.35-2.107-.692-3.287-.81-1.094-.111-2.278-.039-3.213.492zM8 1.783C7.015.936 5.587.81 4.287.94c-1.514.153-3.042.672-3.994 1.105A.5.5 0 0 0 0 2.5v11a.5.5 0 0 0 .707.455c.882-.4 2.303-.881 3.68-1.02 1.409-.142 2.59.087 3.223.877a.5.5 0 0 0 .78 0c.633-.79 1.814-1.019 3.222-.877 1.378.139 2.8.62 3.681 1.02A.5.5 0 0 0 16 13.5v-11a.5.5 0 0 0-.293-.455c-.952-.433-2.48-.952-3.994-1.105C10.413.809 8.985.936 8 1.783"/>
                                    </svg>
                                </div>
                                <div>
                                    <h4 class="fw-bold mb-1">Available Books</h4>
                                    <p class="text-secondary small mb-0">Browse library catalog and issue books</p>
                                </div>
                            </div>
                            <p class="text-muted flex-grow-1">
                                Search books by title or author and issue books that are in stock with one click.
                            </p>
                            <a href="${pageContext.request.contextPath}/student/books.jsp" id="browseBooksBtn" class="btn btn-primary w-100 py-2">
                                Browse & Issue Books &rarr;
                            </a>
                        </div>
                    </div>

                    <!-- My Issued Books Card -->
                    <div class="col-12 col-md-6">
                        <div class="card card-custom h-100 p-4">
                            <div class="d-flex align-items-center mb-3">
                                <div class="bg-success bg-opacity-10 text-success p-3 rounded-circle me-3">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" fill="currentColor" class="bi bi-clock-history" viewBox="0 0 16 16">
                                        <path d="M8.515 1.019A7 7 0 0 0 8 1V0a8 8 0 0 1 .589.022zm2.004.45a7 7 0 0 0-.985-.299l.219-.976q.576.129 1.123.344zm2.426 1.705l-.77.638q.43.518.75 1.113l.887-.462q-.38-.71-.867-1.289m1.488 2.505l-.936.349q.287.771.42 1.606l.99-.145q-.157-.992-.474-1.81"/>
                                        <path d="M8.5 5.5a.5.5 0 0 0-1 0v3.362l1.646 1.647a.5.5 0 0 0 .708-.708L8.5 8.362z"/>
                                        <path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0m-1 0A7 7 0 1 0 1 8a7 7 0 0 0 14 0"/>
                                    </svg>
                                </div>
                                <div>
                                    <h4 class="fw-bold mb-1">My Issued Books</h4>
                                    <p class="text-secondary small mb-0">View borrowing history and return books</p>
                                </div>
                            </div>
                            <p class="text-muted flex-grow-1">
                                Check all currently borrowed books and returned history. Return any active book easily.
                            </p>
                            <a href="${pageContext.request.contextPath}/student/issued-books.jsp" id="myIssuedBooksBtn" class="btn btn-outline-success w-100 py-2">
                                View My Borrowed Books &rarr;
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

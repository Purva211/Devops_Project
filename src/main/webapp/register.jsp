<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Registration - Library Management System</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Inter', sans-serif;
            background: linear-gradient(135deg, #f0f4f8 0%, #d9e2ec 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 2rem 1rem;
            margin: 0;
        }
        .register-card {
            background: #ffffff;
            border-radius: 16px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
            border: 1px solid rgba(226, 232, 240, 0.8);
            padding: 2.5rem;
            max-width: 480px;
            width: 100%;
        }
        .header-section {
            text-align: center;
            margin-bottom: 2rem;
        }
        .hero-icon {
            width: 60px;
            height: 60px;
            background: #e0f2fe;
            color: #0284c7;
            border-radius: 50%;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            margin-bottom: 1rem;
        }
        .hero-icon svg {
            width: 30px;
            height: 30px;
        }
        .card-title {
            font-weight: 700;
            color: #1e293b;
            font-size: 1.6rem;
            margin-bottom: 0.35rem;
        }
        .card-subtitle {
            color: #64748b;
            font-size: 0.9rem;
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
        .btn-register {
            background-color: #2563eb;
            border-color: #2563eb;
            color: #ffffff;
            font-weight: 600;
            padding: 0.75rem;
            border-radius: 8px;
            font-size: 1rem;
            width: 100%;
            margin-top: 0.5rem;
            box-shadow: 0 4px 12px rgba(37, 99, 235, 0.25);
            transition: all 0.2s ease;
        }
        .btn-register:hover {
            background-color: #1d4ed8;
            border-color: #1d4ed8;
            color: #ffffff;
            transform: translateY(-1px);
            box-shadow: 0 6px 16px rgba(37, 99, 235, 0.35);
        }
        .footer-links {
            text-align: center;
            margin-top: 1.5rem;
            font-size: 0.9rem;
            color: #64748b;
        }
        .footer-links a {
            color: #2563eb;
            text-decoration: none;
            font-weight: 600;
        }
        .footer-links a:hover {
            text-decoration: underline;
        }
        .badge-role {
            background-color: #f1f5f9;
            color: #475569;
            font-size: 0.75rem;
            font-weight: 600;
            padding: 0.35rem 0.65rem;
            border-radius: 6px;
            border: 1px solid #e2e8f0;
            display: inline-block;
            margin-bottom: 1rem;
        }
    </style>
</head>
<body>
    <div class="register-card">
        <div class="header-section">
            <div class="hero-icon">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.8" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M19 7.5v3m0 0v3m0-3h3m-3 0h-3m-2.25-4.125a3.375 3.375 0 11-6.75 0 3.375 3.375 0 016.75 0zM4 19.235v-.11a6.375 6.375 0 0112.75 0v.109A12.318 12.318 0 0110.374 21c-2.331 0-4.512-.645-6.374-1.765z" />
                </svg>
            </div>
            <h1 class="card-title">Student Registration</h1>
            <p class="card-subtitle">Create your library account to borrow books</p>
            <span class="badge-role">Account Type: Student</span>
        </div>

        <!-- Dynamic Error / Success Messages -->
        <% 
            String error = (String) request.getAttribute("errorMessage");
            if (error == null) {
                error = request.getParameter("error");
            }
            if (error != null && !error.isEmpty()) { 
        %>
            <div class="alert alert-danger alert-dismissible fade show py-2 px-3 mb-3" role="alert">
                <small><%= error %></small>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close" style="padding: 0.75rem;"></button>
            </div>
        <% } %>

        <% 
            String success = (String) request.getAttribute("successMessage");
            if (success == null) {
                success = request.getParameter("success");
            }
            if (success != null && !success.isEmpty()) { 
        %>
            <div class="alert alert-success alert-dismissible fade show py-2 px-3 mb-3" role="alert">
                <small><%= success %></small>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close" style="padding: 0.75rem;"></button>
            </div>
        <% } %>

        <form id="registrationForm" action="RegisterServlet" method="POST" novalidate>
            <!-- Full Name -->
            <div class="mb-3">
                <label for="name" class="form-label">Full Name</label>
                <input type="text" class="form-control" id="name" name="name" value="${not empty name ? name : ''}" placeholder="John Doe" required autofocus>
                <div class="invalid-feedback">Please enter your full name.</div>
            </div>

            <!-- Email -->
            <div class="mb-3">
                <label for="email" class="form-label">Email Address</label>
                <input type="email" class="form-control" id="email" name="email" value="${not empty email ? email : ''}" placeholder="name@example.com" required>
                <div class="invalid-feedback">Please provide a valid email address.</div>
            </div>

            <!-- Password -->
            <div class="mb-3">
                <label for="password" class="form-label">Password</label>
                <input type="password" class="form-control" id="password" name="password" placeholder="At least 6 characters" minlength="6" required>
                <div class="invalid-feedback">Password must be at least 6 characters.</div>
            </div>

            <!-- Confirm Password -->
            <div class="mb-3">
                <label for="confirmPassword" class="form-label">Confirm Password</label>
                <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" placeholder="Re-enter your password" minlength="6" required>
                <div id="passwordMismatchFeedback" class="invalid-feedback">Passwords do not match.</div>
            </div>

            <!-- Submit Button -->
            <button type="submit" id="registerSubmitBtn" class="btn btn-register">Register</button>
        </form>

        <div class="footer-links">
            <div>Already have an account? <a href="login.jsp" id="loginLink">Login here</a></div>
            <div class="mt-2"><a href="index.jsp" class="text-secondary small">&larr; Back to Home</a></div>
        </div>
    </div>

    <!-- Bootstrap JS Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>

    <!-- Client-side Validation Script -->
    <script>
        const form = document.getElementById('registrationForm');
        const password = document.getElementById('password');
        const confirmPassword = document.getElementById('confirmPassword');

        form.addEventListener('submit', function (event) {
            let isValid = true;

            // Password confirmation check
            if (password.value !== confirmPassword.value) {
                confirmPassword.setCustomValidity("Passwords do not match");
                isValid = false;
            } else {
                confirmPassword.setCustomValidity("");
            }

            if (!form.checkValidity() || !isValid) {
                event.preventDefault();
                event.stopPropagation();
            }

            form.classList.add('was-validated');
        }, false);

        // Real-time password matching feedback
        confirmPassword.addEventListener('input', function () {
            if (password.value !== confirmPassword.value) {
                confirmPassword.setCustomValidity("Passwords do not match");
            } else {
                confirmPassword.setCustomValidity("");
            }
        });
    </script>
</body>
</html>

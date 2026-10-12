<<<<<<< HEAD
# 📚 Library Management System (DevOps Project)

A complete Java web-based **Library Management System** containerized with **Docker**, orchestrated using **Docker Compose**, and equipped with automated **CI/CD** testing and packaging via **Jenkins**.

---

## 🚀 Features

- **Authentication & Authorization**:
  - Role-Based Access Control (`ADMIN` and `STUDENT`).
  - Secure registration, login, session management, and logout.
- **Book Catalogue Management (Admin)**:
  - Add new books (Title, Author, Category, Quantity).
  - Track total vs. available book stock dynamically.
- **Issue & Return Workflow**:
  - Students can browse available catalogue books and issue them.
  - Return books with real-time stock quantity updates.
  - View personal issued book history.
- **Responsive UI**:
  - Styled with Bootstrap 5.3 and custom modern themes.
- **Automated CI/CD & Containerization**:
  - Multi-stage Docker build producing a lightweight Apache Tomcat 9 production image.
  - Docker Compose service definition linking MySQL 8.0 and the Tomcat web app.
  - Automated Jenkins pipeline for continuous compilation, unit testing, and WAR packaging.

---

## 🛠️ Technology Stack

| Layer | Technology |
|---|---|
| **Language & Runtime** | Java 8 (OpenJDK / Eclipse Temurin) |
| **Web Framework** | Java Servlets (3.1.0), JSP |
| **Persistence / Database** | JDBC, MySQL 8.0 (`library_db`) |
| **Frontend UI** | JSP, Bootstrap 5.3.3, Google Fonts |
| **Testing** | JUnit Jupiter 5.10.2, Mockito 4.11.0 |
| **Build & Dependency Tool** | Apache Maven 3.9+ |
| **Application Server** | Apache Tomcat 9.0 |
| **Containerization** | Docker (Multi-stage build), Docker Compose |
| **CI/CD Automation** | Jenkins Declarative Pipeline (`Jenkinsfile`) |

---

## 📁 Project Structure

```text
Devops_Project/
├── Dockerfile                  # Multi-stage Docker build (Maven build + Tomcat 9 runtime)
├── docker-compose.yml          # Container orchestration (Web app + MySQL 8.0)
├── .dockerignore               # Build context ignore rules
├── Jenkinsfile                 # CI pipeline (Checkout -> Build -> Test -> Package)
├── library_db_setup.sql        # Database schema and seed data
├── pom.xml                     # Maven project configuration and dependencies
└── src/
    ├── main/
    │   ├── java/com/library/
    │   │   ├── dao/            # Data Access Objects (UserDAO, BookDAO, IssueDAO)
    │   │   ├── filter/         # Security & authentication filters
    │   │   ├── model/          # Entities (User, Book, Issue)
    │   │   ├── servlet/        # HTTP Servlets (Login, Register, Book, Issue, etc.)
    │   │   └── util/           # DBConnection with environment variable overrides
    │   └── webapp/             # JSP pages, CSS, and web.xml
    └── test/
        └── java/com/library/   # Unit and servlet mock tests (JUnit 5 + Mockito)
```

---

## 🐳 Quick Start with Docker

### 1. Run Everything with Docker Compose (Recommended)

Run both MySQL 8.0 and the web application with a single command:

```bash
docker compose up -d --build
```

- **Web Application**: Accessible at `http://localhost:8085/` or `http://localhost:8085/LibraryManagementSystem/`
- **MySQL Database**: Running on port `3307` (`library_db`)
- Automatic database schema and sample data initialization via `library_db_setup.sql`.

To stop the containers:
```bash
docker compose down
```

---

### 2. Standalone Docker Image Build & Run

#### Build Docker Image:
```bash
docker build -t library-management-system:latest .
```

#### Run the Container:
```bash
docker run -d -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e DB_PORT=3306 \
  -e DB_NAME=library_db \
  -e DB_USER=root \
  -e DB_PASSWORD=root \
  --name library-app \
  library-management-system:latest
```

Application URL: `http://localhost:8080/`

---

## 💻 Local Development Setup (Without Docker)

### Prerequisites
- JDK 8 or higher
- Apache Maven 3.8+
- MySQL Server running locally on port `3306`

### 1. Database Setup
Import the database schema:
```bash
mysql -u root -p < library_db_setup.sql
```

### 2. Run with Maven Tomcat Plugin
```bash
mvn clean compile
mvn tomcat7:run
```
Access the application at `http://localhost:8085/LibraryManagementSystem/`.

### 3. Run Unit Tests
```bash
mvn test
```

---

## 🔐 Default Credentials

| Role | Email | Password |
|---|---|---|
| **Admin** | `admin@library.com` | `admin123` |
| **Student** | Registered via `/register.jsp` | As configured |

---

## 🔄 CI/CD Pipeline (Jenkins)

The project includes a declarative `Jenkinsfile` configuring:
1. **Checkout**: Retrieves source code from GitHub.
2. **Build**: Compiles source code with `mvn clean compile`.
3. **Test**: Executes unit tests with `mvn test` (JUnit 5 & Mockito).
4. **Package**: Builds the WAR file with `mvn package -DskipTests` and archives `target/*.war`.

---

## ⚙️ Environment Variables

The application dynamically detects configuration via environment variables:

| Variable | Description | Default |
|---|---|---|
| `DB_HOST` | MySQL hostname / service name | `localhost` |
| `DB_PORT` | MySQL port | `3306` |
| `DB_NAME` | Database name | `library_db` |
| `DB_USER` | MySQL user | `root` |
| `DB_PASSWORD` | MySQL password | `root` (fallback: `anu@2505`) |
| `DB_URL` | Complete JDBC URL override | Auto-generated from host/port/name |
=======
# Devops_Project
>>>>>>> 5a80529cd776576a4e321b92d0be00b75715922e

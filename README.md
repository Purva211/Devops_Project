# 📚 Library Management System --- DevOps Project

A Java-based web application for managing library operations, including
user registration, authentication, book catalogue management, issuing
and returning books, and role-based access for administrators and
students. The project demonstrates a DevOps workflow using **Maven,
Docker, Docker Compose, MySQL, and Jenkins**.

> **Project repository:**
> [Purva211/Devops_Project](https://github.com/Purva211/Devops_Project)

## ✨ Features

-   **Authentication:** User registration, login, logout, and
    session-based access.
-   **Role-based access:** Separate Admin and Student experiences.
-   **Book management:** Add, search, update, and delete books (admin
    functions).
-   **Issue and return:** Issue available books, return books, and
    update availability.
-   **Issue history:** View issued-book information.
-   **Relational database:** MySQL persistence through JDBC.
-   **Automated testing:** JUnit 5 and Mockito test dependencies.
-   **Containerization:** Docker image for the application and Docker
    Compose services for the app and database.
-   **CI pipeline:** Jenkins stages for build, test, and WAR packaging.

## 🧰 Tech Stack

  Area         Technologies
  ------------ ------------------------------------------
  Language     Java 8
  Web          Java Servlets, JSP, HTML, CSS, Bootstrap
  Database     MySQL 8, JDBC
  Build        Apache Maven
  App server   Apache Tomcat 9
  Testing      JUnit 5, Mockito
  Containers   Docker, Docker Compose
  CI           Jenkins Declarative Pipeline

## 🗂️ Project Structure

``` text
Devops_Project/
├── Dockerfile
├── docker-compose.yml
├── Jenkinsfile
├── pom.xml
├── library_db_setup.sql
├── .dockerignore
└── src/
    ├── main/
    │   ├── java/com/library/
    │   │   ├── dao/        # Database access classes
    │   │   ├── filter/     # Authentication and access filters
    │   │   ├── model/      # User, Book, and Issue models
    │   │   ├── servlet/    # Authentication and library operations
    │   │   └── util/       # Database connection utility
    │   └── webapp/         # JSP pages and web resources
    └── test/
        └── java/com/library/ # Unit and servlet tests
```

## ✅ Prerequisites

Choose either Docker-based setup or local development.

**Docker setup** - Docker Desktop (Windows/macOS) or Docker Engine -
Docker Compose v2

**Local setup** - JDK 8 - Apache Maven 3.8+ - MySQL 8 - Apache Tomcat 9,
or a configured Maven Tomcat plugin if supported by the project

## 🚀 Run with Docker Compose

Run the following commands from the directory containing
`docker-compose.yml`:

``` bash
docker compose up --build -d
```

Check running containers:

``` bash
docker compose ps
```

Open the application:

-   **Web application:** http://localhost:8085/
-   **Alternative context path (if deployed):**
    http://localhost:8085/LibraryManagementSystem/

The Compose configuration maps MySQL to host port `3307` and the web app
to host port `8085`. The database service uses `library_db`.

View logs:

``` bash
docker compose logs -f
```

Stop the services:

``` bash
docker compose down
```

Stop services and remove the database volume (**this deletes persisted
database data**):

``` bash
docker compose down -v
```

> **Note:** The first MySQL startup initializes the database from the
> configured SQL file. If the database volume already exists, MySQL
> generally will not rerun initialization scripts automatically.

## 💻 Local Development

Run these commands from the directory containing `pom.xml`.

### 1. Configure the database

Create the database and tables using the supplied SQL script:

``` bash
mysql -u root -p < library_db_setup.sql
```

Update the database connection settings used by the application to match
your local MySQL host, port, database, username, and password. Do not
commit real passwords or other credentials to GitHub.

### 2. Build and test

``` bash
mvn clean test
```

Build the deployable WAR file:

``` bash
mvn clean package
```

The WAR artifact is normally created under:

``` text
target/LibraryManagementSystem.war
```

Deploy the WAR to a compatible Tomcat 9 server to run the web
application.

## 🧪 Tests

Run the automated tests with:

``` bash
mvn test
```

To run a clean build including tests and packaging:

``` bash
mvn clean verify
```

## 🐳 Docker Image

Build the image from the directory expected by the Dockerfile:

``` bash
docker build -t library-management-system:latest .
```

If using the standalone image, configure the database environment
variables to match a reachable MySQL instance:

  -----------------------------------------------------------------------
  Variable                Purpose                 Example for Compose
  ----------------------- ----------------------- -----------------------
  `DB_HOST`               MySQL hostname          `mysql`

  `DB_PORT`               MySQL port inside the   `3306`
                          Docker network          

  `DB_NAME`               Database name           `library_db`

  `DB_USER`               Database username       `root`

  `DB_PASSWORD`           Database password       Set locally; do not
                                                  commit secrets
  -----------------------------------------------------------------------

For Docker Compose, the hostname `mysql` works because both services
share a Docker network.

## 🔁 Jenkins CI Pipeline

The repository includes a `Jenkinsfile` with the following stages:

1.  **Checkout** --- identifies the source-control stage.
2.  **Build** --- compiles the Java project with Maven.
3.  **Test** --- runs automated tests.
4.  **Package** --- produces the WAR artifact and archives it.

### Jenkins setup checklist

-   Configure a Jenkins agent with the required JDK and Maven.
-   Configure the Maven tool name referenced in the `Jenkinsfile`
    (currently `Maven3`).
-   Ensure the job checks out this repository and runs from the
    directory containing `pom.xml`.
-   If the Jenkins agent runs Linux, change Windows-specific `bat`
    pipeline steps to `sh`.
-   Verify the archived artifact path matches the actual Maven output
    directory.

## 🔐 Configuration and Security

-   Configure database credentials through environment variables or a
    secure secret store where supported.
-   Never commit API keys, passwords, private keys, or production
    credentials.
-   Change any sample/default credentials before using the application
    outside a local demo.
-   Use HTTPS and production-grade session/security settings before
    deployment.

## 🛠️ Troubleshooting

**Docker containers are running but the application cannot connect to
MySQL** - Check `docker compose ps` and
`docker compose logs mysql web`. - Confirm the app uses `DB_HOST=mysql`
and `DB_PORT=3306` inside Compose. - Wait for the MySQL health check to
pass.

**Port already in use** - Stop the process using the conflicting port or
update the port mapping in `docker-compose.yml`.

**Maven build fails** - Check the installed Java and Maven versions with
`java -version` and `mvn -version`. - Run `mvn clean test` and review
the first compilation or test error.

**Changes to the SQL initialization script are not applied** - MySQL
initialization scripts run when the data directory is first initialized.
Back up any data before considering removal of the Compose volume.

## 🎯 Learning Outcomes

This project demonstrates how a Java web application can be built,
tested, packaged, containerized, connected to a relational database, and
prepared for a Jenkins-based CI workflow.


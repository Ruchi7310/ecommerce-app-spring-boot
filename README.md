# ecommerce-app-spring-boot
# E-Commerce Application – Spring Boot

A backend e-commerce application built using Java and Spring Boot, designed to manage products, users, authentication, and order processing through REST APIs.

## 🚀 Technologies Used

- **Language:** Java
- **Backend Framework:** Spring Boot
- **Database:** PostgreSQL
- **ORM:** Spring Data JPA / Hibernate
- **Security:** Spring Security, JWT Authentication, BCrypt Password Hashing
- **API Testing:** Postman
- **Build Tool:** Maven
- **Version Control:** Git and GitHub

## ✨ Features

- User registration and login
- JWT-based authentication
- Role-based authorization for users and administrators
- Product management APIs
- Order and order-item management
- Password hashing using BCrypt
- RESTful API development
- PostgreSQL database integration

## 🏗️ Project Architecture

The application follows a layered backend architecture:

- **Controller Layer:** Handles incoming HTTP requests and API responses.
- **Service Layer:** Contains business logic.
- **Repository Layer:** Manages database operations using Spring Data JPA.
- **Entity Layer:** Represents database entities and relationships.
- **Security Layer:** Handles authentication and authorization.

## ⚙️ Getting Started

### Prerequisites

- Java JDK 21
- Maven
- PostgreSQL
- Git

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/Ruchi7310/ecommerce-app-spring-boot.git
   ```

2. Open the project in IntelliJ IDEA or your preferred Java IDE.

3. Configure your PostgreSQL database and environment variables.

4. Set the following environment variables before running the application:

   - `DB_PASSWORD` — your PostgreSQL database password
   - `JWT_SECRET` — a secure JWT signing secret

5. Build and run the application:

   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

6. Test the available REST APIs using Postman.

## 🔐 Security

- Passwords are hashed using BCrypt.
- JWT is used for authentication.
- Role-based access control protects authorized operations.
- Database passwords and JWT signing secrets should be supplied through environment variables and must not be committed to GitHub.

## 👩‍💻 Author

**Ruchi Chaudhary**

BCA Graduate | Aspiring Java Backend Developer

- GitHub: [Ruchi7310](https://github.com/Ruchi7310)

---

*This project demonstrates backend development skills using Java, Spring Boot, REST APIs, database integration, and application security.*

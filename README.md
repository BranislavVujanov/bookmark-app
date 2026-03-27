# Bookmark App

A Spring Boot REST API for managing bookmarks with authentication, role-based access control, and robust validation.

---

## 📝 Project Overview

This application allows authenticated users to manage bookmarks with strict access control and business rules.

### 🔐 Access Rules

* **USER**

  * Can create, read, update, and delete **their own bookmarks only**

* **MODERATOR**

  * Can view **all bookmarks**
  * Can delete **any bookmark**
  * Cannot create or update bookmarks

---

## ⚙️ Technologies Used

* **Java 17**
* **Spring Boot 3.x**
* **Spring Web (REST API)**
* **Spring Security (HTTP Basic Authentication)**
* **Spring Data JDBC**
* **H2 Database**
* **Gradle**
* **NetBeans IDE**
* **Git & GitHub**

---

## 🗄️ Database Design

### USERS table

* Stores authentication and roles
* Fields: `id`, `username`, `password`, `role`, `enabled`

### BOOKMARK table

* Stores user bookmarks
* Fields: `id`, `title`, `url`, `description`, `created_at`, `user_id`
* Foreign key → USERS(id)
* Unique constraint → `(user_id, url)` (prevents duplicates)

---

## 🚀 Getting Started

### Prerequisites

* Java 17 installed
* Git installed

### Run Locally

1. Clone the repository:

git clone https://github.com/BranislavVujanov/bookmark-app.git
cd bookmark-app

2. Run the application:

./gradlew bootRun   # Linux / Mac
gradlew.bat bootRun  # Windows

3. API available at:

http://localhost:8080

---

## 🔐 Authentication

Authentication is handled via **HTTP Basic Auth**.

### Example users:

| Username  | Password | Role      | Enabled      |
| --------- | -------- | --------- | ------------ |
| anna      | xyz123   | USER      | ✅            |
| john      | abc123   | USER      | ✅            |
| moderator | mod123   | MODERATOR | ✅            |
| mika      | asd131   | USER      | ❌ (disabled) |

---

## 🛠 API Usage

All endpoints require authentication.

### Endpoints:

* `GET /bookmarks` → List bookmarks (paginated)
* `GET /bookmarks/{id}` → Get bookmark by ID
* `POST /bookmarks` → Create bookmark
* `PUT /bookmarks/{id}` → Update bookmark
* `DELETE /bookmarks/{id}` → Delete bookmark

### Features:

* Pagination and sorting supported
  Example:
  `/bookmarks?page=0&size=2&sort=id,asc`

---

## 🧠 Key Features

* Role-based authorization (USER vs MODERATOR)
* Per-user data isolation
* Moderator-level global access (read/delete)
* Duplicate bookmark prevention (DB + service layer)
* Input validation with custom exceptions
* Disabled user authentication handling
* Pagination and sorting support
* Comprehensive integration and JSON tests

---

## 🧪 Testing

The project includes:

* JSON serialization/deserialization tests
* Full integration tests using `TestRestTemplate`
* Authentication and authorization tests
* Validation and error handling tests

---

## 📂 Project Structure

src/          # Application source code
build.gradle  # Gradle configuration
gradlew       # Gradle wrapper
.gitignore    # Git ignore rules
README.md     # Project documentation

---

## 🔗 GitHub

https://github.com/BranislavVujanov/bookmark-app



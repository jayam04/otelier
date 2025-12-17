# Otelier – Hotel Booking Backend (Spring Boot)

A secure, production-ready backend service for hotel booking management built using **Java Spring Boot**, **MongoDB Atlas**, and **Supabase Authentication**.

This project was implemented as a mini-product, focusing on clean architecture, security, and real-world backend design patterns.

---

## 🚀 Tech Stack

* **Java 17**
* **Spring Boot 3**
* **Spring Web**
* **Spring Security**
* **Spring Data MongoDB**
* **MongoDB Atlas**
* **Supabase Auth (JWT-based authentication)**
* **Swagger / OpenAPI**
* **Slack & Email notifications**

---

## 📐 Architecture Overview

```
Client
  └── Supabase Auth (Login)
        └── JWT (access_token)
              └── Spring Boot Backend
                    ├── Security (JWT validation)
                    ├── Authorization (Hotel-level roles)
                    ├── MongoDB Atlas
                    ├── Slack / Email notifications
```

* Authentication is handled by **Supabase**
* Backend validates Supabase JWTs
* Authorization is enforced **per hotel**
* Notifications are sent on booking creation

---

## 🔐 Authentication & Authorization

### Authentication

* Users authenticate directly with **Supabase**
* Client sends Supabase `access_token` to backend
* Backend validates:

  * JWT signature
  * Issuer
  * Expiry
* User ID is extracted from `sub` claim

### Role Handling

* Admin roles are extracted from:

  ```
  app_metadata.roles
  ```
* Example:

  ```json
  "app_metadata": {
    "roles": ["ADMIN"]
  }
  ```

### Authorization Model

* Hotel access is managed via a dedicated collection:

  ```
  hotel_assignments
  ```
* Supports:

  * Multiple users per hotel
  * Multiple hotels per user
  * Role-based access per hotel (admin, manager, staff, reception)

---

## 🧠 Key Design Decisions

### Hotel Assignments vs Embedded Users

Instead of embedding user IDs inside the `hotels` collection, a separate `hotel_assignments` collection is used.

**Why:**

* Supports many-to-many relationships
* Enables role-based access per hotel
* Avoids unbounded document growth
* Keeps authorization logic clean and queryable

This approach aligns with real-world hospitality systems.

---

## 📦 Data Model (Simplified)

### Hotel

```json
{
  "id": "hotel-001",
  "name": "Otelier Grand",
  "address": "Mumbai"
}
```

### Booking

```json
{
  "hotelId": "hotel-001",
  "guestName": "John Doe",
  "roomNumber": "101",
  "checkInDate": "2025-01-10",
  "checkOutDate": "2025-01-12",
  "status": "CONFIRMED"
}
```

### Hotel Assignment

```json
{
  "userId": "supabase-user-id",
  "hotelId": "hotel-001",
  "role": "staff"
}
```

---

## 🔌 API Endpoints

### Health

```
GET /health
```

---

### Hotels (Admin only)

#### Create Hotel

```
POST /api/hotels
Authorization: Bearer <ADMIN_JWT>
```

---

### Hotel Assignments (Admin only)

#### Assign User to Hotel

```
POST /api/hotel-assignments
Authorization: Bearer <ADMIN_JWT>
```

---

### Bookings

#### List Bookings

```
GET /api/hotels/{hotelId}/bookings
Authorization: Bearer <JWT>
```

Supports optional date filtering.

#### Create Booking

```
POST /api/hotels/{hotelId}/bookings
Authorization: Bearer <JWT>
```

Requires `staff` or `reception` role.

Includes:

* Date validation
* Conflict detection
* Notifications

---

## 🔔 Notifications

### Slack

* Triggered when a booking is created
* Uses Slack Incoming Webhooks
* Non-blocking and failure-safe

### Email

* Sends booking notification to support email
* Uses Spring Boot Mail
* Optional (disabled if not configured)

---

## 🧪 API Documentation (Swagger)

Swagger UI is enabled for easy testing and review.

```
http://localhost:8080/swagger-ui/index.html
```

Supports:

* JWT Authorization
* Try-it-out for secured endpoints

---

## ⚙️ Configuration & Environment Variables

All secrets are managed via **environment variables**.

### Required Environment Variables

```bash
# MongoDB
MONGODB_URI=

# Supabase
SUPABASE_URL=
SUPABASE_ANON_KEY=
SUPABASE_JWT_SECRET=
SUPABASE_ISSUER=

# Slack
NOTIFICATION_SLACK_WEBHOOK_URL=

# Email (optional)
SPRING_MAIL_USERNAME=
SPRING_MAIL_PASSWORD=
SUPPORT_EMAIL=
```

No secrets are committed to the repository.

---

## ▶️ Running Locally

```bash
./mvnw spring-boot:run
```

Ensure environment variables are set before starting.

---

## 🎯 Assignment Coverage

### Core Requirements

* ✅ JWT authentication
* ✅ MongoDB Atlas integration
* ✅ Booking APIs (GET + POST)
* ✅ Conflict detection
* ✅ Notifications (Slack & Email)
* ✅ Clean error handling
* ✅ Swagger documentation

### Bonus

* ✅ Structured logging
* ✅ Role-based authorization
* ✅ Clean architecture
* ⏳ Docker (optional)
* ⏳ CI/CD (optional)

---

## 🧠 Notes

* The system is designed as a **mini-product**, not just a demo.
* Assumptions were made where appropriate and documented.
* The backend is fully explainable and debuggable.

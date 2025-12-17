# Otelier Backend Assignment

## Overview

This project is a backend service for managing hotels and bookings with role-based access control. It uses **Spring Boot**, **MongoDB**, and **Supabase Auth (JWT-based authentication)** to securely manage users, hotels, assignments, and bookings.

The system is designed to support **many-to-many relationships** between users and hotels, enabling flexible role management such as `ADMIN` and `staff`.

---

## Tech Stack

* **Backend:** Java, Spring Boot
* **Database:** MongoDB (Atlas)
* **Authentication:** Supabase Auth + JWT
* **Build Tool:** Maven
* **Security:** Role-based access control

---

## System Design

### Authentication Layer

* Authentication is handled by **Supabase Auth**.
* JWT tokens are used to authorize API requests.
* The `ADMIN` role is embedded directly in the auth layer instead of MongoDB for increased security.

### Database Design (MongoDB)

Collections:

* `hotels`
* `hotel_assignments`
* `bookings`

> Note: A `users` table is **not used** by the application. User data is managed by Supabase Auth and derived from JWT claims. Any `users` table mentioned in the assignment document exists **only for testing and reviewer verification purposes**.

---

## Database Schema (Sample)

| user_id | UUID from Supabase |
|---|------------|
| email | User email |
| password | Encrypted password |
| is_admin | Boolean flag |
| hotels | Assigned hotel IDs |

### hotels

| Field      | Description        |
| ---------- | ------------------ |
| id         | Hotel ID           |
| name       | Hotel name         |
| address    | Hotel address      |
| created_at | Creation timestamp |

---

## API Endpoints

### Health Check

**GET** `/health`

* Checks whether the backend is running correctly.

---

### Create / Update Hotel

**POST** `/api/hotels`

**Access:** `ADMIN`

**Request Body:**

```json
{
  "id": "hotel-001",
  "name": "Otelier Grand",
  "address": "Mumbai, India"
}
```

**Errors:**

* User is not an ADMIN

---

### Assign Hotel to User

**POST** `/api/hotel-assignments`

**Access:** `ADMIN`

**Request Body:**

```json
{
  "userId": "<uuid>",
  "hotelId": "hotel-001",
  "role": "staff"
}
```

**Errors:**

* Not an ADMIN
* Hotel already assigned
* Hotel does not exist

---

### Get Assigned Hotels

**GET** `/api/hotel-assignments/my-hotels`

**Access:** Authenticated user

Returns a list of hotels the user is assigned to.

---

### Create Booking

**POST** `/api/hotels/{hotel-id}/bookings`

**Access:** `staff` role for the given hotel

**Request Body:**

```json
{
  "guestName": "John Doe",
  "guestEmail": "john@example.com",
  "roomNumber": 101,
  "checkInDate": "2025-01-10",
  "checkOutDate": "2025-01-12"
}
```

**Errors:**

* No access role for hotel
* Booking conflict

---

### Get Bookings

**GET** `/api/hotels/{hotel-id}/bookings`

**Access:** `staff` role for the given hotel

**Errors:**

* No access role

---

## Project Structure

```
src/main/java/space/jayampatel/otelier
├── config        # Application and security configuration
├── controller    # REST controllers
├── dto           # Request/response DTOs
├── exception     # Custom exceptions and handlers
├── model         # MongoDB models
├── repository    # MongoDB repositories
├── security      # JWT and Supabase security logic
├── service       # Business logic
└── OtelierApplication.java
```

---

## Configuration

### application.properties

All sensitive configuration is stored in `application.properties`:

* MongoDB connection string
* Database password
* Supabase configuration

Example:

```
mongodb.uri=mongodb+srv://superuser:<db_password>@main.zw5svjt.mongodb.net
```

---

## Running the Project

1. Clone the repository
2. Configure `application.properties`
3. Run the application:

```bash
mvn spring-boot:run
```

---

## References

* Supabase Auth with Spring Boot: [https://medium.com/@curteisyang/implementing-supabase-authentication-in-spring-boot-6eb5ddaabfc7](https://medium.com/@curteisyang/implementing-supabase-authentication-in-spring-boot-6eb5ddaabfc7)

# API Documentation

## 1. Introduction

**Purpose:** This document defines the structure and functionality of the LeanX ERP-System backend API.

**Scope:** The backend API currently handles authentication, employee management, and employee self-services.

**Target Audience:** Developers.

## 2. Authentication & Session Management (Auth Controller)

Handles user login/logout, password and session management.

### Base URL

`/api/auth`

### Endpoints

#### 2.1 Check Session Status

* **Route:** `/api/auth/session`
* **Method:** `GET`
* **Description:** Checks if a valid user session exists.
* **Request:**
    * No request body.
    * The presence of a valid `JSESSIONID` cookie in the request headers indicates an existing session.
* **Response:**

    **200 OK:**
    ```json
    {
       "status": "Authorized Session!"
    }
    ```
    Indicates that a valid session exists.

    **401 Unauthorized:**
    ```json
    {
      "message": "Invalid or Unauthorized Session!"
    }
    ```
    Indicates that no valid session exists or the user is not authorized.

    **404 Not Found:**
    ```json
    {
      "message": "Unknown endpoint!"
    }
    ```
    Indicates an incorrect endpoint was requested.

#### 2.2 Login

* **Route:** `/api/auth/login`
* **Method:** `POST`
* **Description:** Authenticates a user and establishes a session.
* **Request Body:**
    * **Content-Type:** `application/json`, `application/x-www-form-urlencoded`, or `multipart/form-data`
    * **Parameters:**
        * `username` (string, required): The username of the user.
        * `password` (string, required): The password of the user.
    * **Example (JSON):**
        ```json
        {
          "username": "testuser",
          "password": "securepassword"
        }
        ```
    * **Example (Form Data or URL Encoded):**

        `username=testuser&password=securepassword`
* **Response:**

    **200 OK:**
    ```json
    {
      "status": "Login successful"
    }
    ```
    Indicates successful login and session creation. A `JSESSIONID` cookie will be set in the response headers.

    **200 OK (First Login or Password Expired):**
    ```json
    {
      "tempToken": "encodedTemporaryToken",
      "reason": "First login. Please change your password."
    }
    ```
    or
    ```json
    {
      "tempToken": "encodedTemporaryToken",
      "reason": "Your password has expired. Please change your password."
    }
    ```
    Indicates that the user needs to change their password upon first login or due to password expiration. A temporary token is provided for the password change process, and a `JSESSIONID` cookie is set with a shorter lifespan.

    **400 Bad Request:**
    ```json
    {
      "message": "Invalid JSON data."
    }
    ```
    or
    ```json
    {
      "message": "Fields cannot be null."
    }
    ```
    Indicates that the request body is malformed or missing required fields.

    **401 Unauthorized:**
    ```json
    {
      "message": "Incorrect username or password. Please Try again!"
    }
    ```
    or
    ```json
    {
      "message": "Account is locked."
    }
    ```
    or
    ```json
    {
      "message": "Account is deactivated."
    }
    ```
    Indicates that the provided credentials are incorrect or the account is locked or deactivated. The specific message will vary based on the reason for failure.

#### 2.3 Logout

* **Route:** `/api/auth/logout`
* **Method:** `POST`
* **Description:** Invalidates the current user session.
* **Request:**
    * No request body.
    * A valid `JSESSIONID` cookie should be present in the request headers.
* **Response:**

    **200 OK:**
    ```json
    {
      "status": "Logout successful!"
    }
    ```
    Indicates that the session has been successfully terminated. The `JSESSIONID` cookie will be invalidated.

#### 2.4 Change Password

* **Route:** `/api/auth/change-password`
* **Method:** `POST`
* **Description:** Allows a user to change their password after a first login or password expiration, using a temporary token.
* **Request Body:**
    * **Content-Type:** `application/json`, `application/x-www-form-urlencoded`, or `multipart/form-data`
    * **Parameters:**
        * `newPassword` (string, required): The new password.
        * `confirmNewPassword` (string, required): Confirmation of the new password.
        * `token` (string, required): The temporary token received during the login response for first login or password expiration.
    * **Example (JSON):**
        ```json
        {
          "newPassword": "newSecurePassword",
          "confirmNewPassword": "newSecurePassword",
          "token": "encodedTemporaryToken"
        }
        ```
    * **Example (Form Data or URL Encoded):**
        `newPassword=newSecurePassword&confirmNewPassword=newSecurePassword&token=encodedTemporaryToken`
* **Response:**

    **200 OK:**
    ```json
    {
      "status": "Password changed successfully!"
    }
    ```
    Indicates that the password has been successfully updated, and the session is invalidated (user will need to log in again with the new password).

    **400 Bad Request:**
    ```json
    {
      "message": "Invalid JSON data."
    }
    ```
    or
    ```json
    {
      "message": "Missing or invalid input."
    }
    ```
    or
    ```json
    {
      "message": "New password cannot be the same as the old password."
    }
    ```
    or other validation error messages.

    **401 Unauthorized:**
    ```json
    {
      "message": "Unauthorized access to change password."
    }
    ```
    Indicates that there is no active temporary token session.
    or
    ```json
    {
      "message": "Invalid or missing token for password change."
    }
    ```
    Indicates that the provided temporary token is invalid or missing.

    **500 Internal Server Error:**
    ```json
    {
      "message": "Failed to update password!"
    }
    ```
    Indicates an unexpected error occurred on the server while attempting to change the password.

## 3. Employee Management (Employee Controller)

Handles the creation, retrieval, and modification of employee records.

### Base URL

`/api/employee`

### Endpoints

#### 3.1 Create Employee

* **Route:** `/api/employee`
* **Method:** `POST`
* **Description:** Creates a new employee record.
* **Request Body:**
    * **Content-Type:** `application/json`
    * **Request Body Schema:**
        ```json
        {
          "firstName": "string",
          "lastName": "string",
          "email": "string",
          "managerId": "integer"
          // ... other employee fields
        }
        ```
    * **Example:**
        ```json
        {
          "firstName": "John",
          "lastName": "Doe",
          "email": "[email address removed]",
          "managerId": 123
        }
        ```
* **Response:**

    **200 OK:**
    ```json
    {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe",
      "email": "[email address removed]",
      "managerId": 123,
      "createdBy": 456
      // ... other employee fields with generated ID and createdBy
    }
    ```
    Indicates successful creation of the employee record. The response body contains the newly created employee object, including the generated ID and the ID of the user who created it (from the session).

    **400 Bad Request:**
    ```json
    {
      "message": "Missing required employee fields."
    }
    ```
    or
    ```json
    {
      "message": "Error creating the employee record!"
    }
    ```
    Indicates that the request body is missing required fields or there was an error during the creation process.

    **500 Internal Server Error:**
    ```json
    {
      "message": "Database error occurred."
      // ... other details if available
    }
    ```
    Indicates a database error occurred during the operation.

#### 3.2 Update Employee

* **Route:** `/api/employee/{id}`
* **Method:** `PUT`
* **Description:** Updates an existing employee record with the specified ID.
* **Path Parameter:**
    * `{id}` (integer, required): The ID of the employee to update.
* **Request Body:**
    * **Content-Type:** `application/json`
    * **Request Body Schema:**
        ```json
        {
          "firstName": "string",
          "lastName": "string",
          "email": "string",
          "managerId": "integer"
          // ... other employee fields that need to be updated
        }
        ```
    * **Example:**
        ```json
        {
          "firstName": "Johnny",
          "managerId": 456
        }
        ```
* **Response:**

    **200 OK:**
    ```json
    {
      "id": 1,
      "firstName": "Johnny",
      "lastName": "Doe",
      "email": "[email address removed]",
      "managerId": 456,
      "lastUpdatedBy": 789
      // ... other updated employee fields with the ID of the user who updated it
    }
    ```
    Indicates successful update of the employee record. The response body contains the updated employee object, including the ID of the user who performed the update.

    **400 Bad Request:**
    ```json
    {
      "message": "Invalid employee ID."
    }
    ```
    Indicates that the provided employee ID in the path is not a valid integer.

    **404 Not Found:**
    ```json
    {
      "message": "Employee not found."
    }
    ```
    Indicates that no employee record exists with the specified ID.

    **500 Internal Server Error:**
    ```json
    {
      "message": "Database error occurred."
      // ... other details if available
    }
    ```
    Indicates a database error occurred during the operation.

#### 3.3 Get All Employees

* **Route:** `/api/employee`
* **Method:** `GET`
* **Description:** Retrieves a list of all employee records.
* **Request:**
    * No request body or parameters.
* **Response:**

    **200 OK:**
    ```json
    [
      {
        "id": 1,
        "firstName": "John",
        "lastName": "Doe",
        "email": "[email address removed]",
        "managerId": 123
        // ... other employee fields
      },
      {
        "id": 2,
        "firstName": "Jane",
        "lastName": "Smith",
        "email": "[email address removed]",
        "managerId": 123
        // ... other employee fields
      }
      // ... more employee objects
    ]
    ```
    Indicates successful retrieval of all employee records. The response body contains a JSON array of employee objects.

    **500 Internal Server Error:**
    ```json
    {
      "message": "Failed to fetch employees."
      // ... other details if available
    }
    ```
    Indicates a database error occurred while fetching the employees.

#### 3.4 Search Employees by Name

* **Route:** `/api/employee/search`
* **Method:** `GET`
* **Description:** Searches for employee records whose first or last name contains the provided query.
* **Query Parameter:**
    * `name` (string, required): The name or part of the name to search for.
* **Request:**
    * Example: `/api/employee/search?name=john`
* **Response:**

    **200 OK:**
    ```json
    [
      {
        "id": 1,
        "firstName": "John",
        "lastName": "Doe",
        "email": "[email address removed]",
        "managerId": 123
        // ... other employee fields
      },
      {
        "id": 3,
        "firstName": "Johnathan",
        "lastName": "Miller",
        "email": "[email address removed]",
        "managerId": 456
        // ... other employee fields
      }
      // ... matching employee objects
    ]
    ```
    Indicates successful retrieval of employee records matching the search query. The response body contains a JSON array of matching employee objects.

    **400 Bad Request:**
    ```json
    {
      "message": "Missing search query."
    }
    ```
    Indicates that the `name` query parameter is missing or empty.

    **500 Internal Server Error:**
    ```json
    {
      "message": "Database error while searching employees."
      // ... other details if available
    }
    ```
    Indicates a database error occurred during the search operation.

#### 3.5 Get Employee by ID

* **Route:** `/api/employee/{id}`
* **Method:** `GET`
* **Description:** Retrieves a specific employee record by their ID.
* **Path Parameter:**
    * `{id}` (integer, required): The ID of the employee to retrieve.
* **Request:**
    * Example: `/api/employee/123`
* **Response:**

    **200 OK:**
    ```json
    {
      "id": 123,
      "firstName": "Jane",
      "lastName": "Smith",
      "email": "[email address removed]",
      "managerId": 789
      // ... other employee fields
    }
    ```
    Indicates successful retrieval of the employee record. The response body contains the requested employee object.

    **400 Bad Request:**
    ```json
    {
      "message": "Invalid employee ID format."
    }
    ```
    Indicates that the provided employee ID in the path is not a valid integer.

    **404 Not Found:**
    ```json
    {
      "message": "Employee not found."
    }
    ```
    Indicates that no employee record exists with the specified ID.

    **500 Internal Server Error:**
    ```json
    {
      "message": "Database error while fetching employee."
      // ... other details if available
    }
    ```
    Indicates a database error occurred while fetching the employee.

## 4. Employee Self-Service (EmployeeSelfController)

Handles the retrieval of personal employee information for the logged-in user.

### Base URL

`/api/employee/self`

### Endpoints

#### 4.1 Get Personal Employee Profile

* **Route:** `/api/employee/self`
* **Method:** `GET`
* **Description:** Retrieves the personal employee profile for the currently logged-in user.
* **Request:**
    * No request body.
    * A valid `JSESSIONID` cookie should be present in the request headers, identifying the logged-in user. The `userId` attribute from the session is used to fetch the profile.
* **Response:**

    **200 OK:**
    ```json
    {
      "id": 456,
      "firstName": "Current",
      "lastName": "User",
      "email": "[email address removed]",
      "managerFirstName": "Jane",
      "managerLastName": "Doe",
      "jobTitle": "Senior Developer",
      "department": "IT",
      "employmentType": "FULL_TIME",
      "employmentStatus": "ACTIVE",
      "startDate": "2024-01-15"
    }
    ```
    Indicates successful retrieval of the personal employee profile. The response body contains the employee profile information for the logged-in user.

    **400 Bad Request:**
    ```json
    {
      "message": "Error fetching personal employee profile!"
    }
    ```
    Indicates an error occurred while fetching the personal employee profile, possibly due to an invalid user ID in the session.

    **404 Not Found:**
    ```json
    {
      "message": "Unknown endpoint!"
    }
    ```
    Indicates an incorrect endpoint was requested under `/api/employee/self`.

    **500 Internal Server Error:**
    ```json
    {
      "message": "Database error occurred."
      // ... other details if available
    }
    ```
    Indicates a database error occurred during the operation.
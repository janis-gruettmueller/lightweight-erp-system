1. Authentication & Session Management (Auth Module)

Handles user login/logout and session management.

# Authentication API

This document describes the API endpoints for user authentication.

## Base URL

`/api/auth`

## Endpoints

### 1. Check Session Status

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

### 2. Login

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

### 3. Logout

* **Route:** `/api/auth/logout`
* **Method:** `POST`
* **Description:** Invalidates the current user session.
* **Request:**
    * No request body.
    * A valid `JSESSIONID` cookie should be present in the request headers.
* **Response:**

    **200 OK:**
    ```json
    "Logout successful!"
    ```
    Indicates that the session has been successfully terminated. The `JSESSIONID` cookie will be invalidated.

### 4. Change Password

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
    "Password changed successfully!"
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

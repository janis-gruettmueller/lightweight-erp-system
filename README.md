# LeanX ERP-System

## Introduction

LeanX is a lightweight ERP system designed for efficient management of core business processes. This document provides an overview of the system's architecture, API, setup and database.
The productive environment is currently accessible under [www.lean-x.de](https://www.lean-x.de) or [lean-x.de](https://lean-x.de).

For demonstration purposes use the following user to gain access to the LeanX environment:

- Username: DEFAULT_USR
- Password: initERP@2025

## Architecture

The LeanX ERP-System is hosted on Amazon Web Services (AWS) and utilizes a containerized architecture within an EC2 instance. Key components include:

* **Frontend:** A Node.js server with a Next.js app for the user interface.
* **Backend:** An Apache Tomcat Server running a Java application. Notably, the backend is built without a specific framework, emphasizing a lightweight and highly customizable approach to application logic.
* **Database:** MySQL (Amazon RDS) for transactional data.
* **Data Warehouse:** Supabase DB for business intelligence capabilities.
* **Reverse Proxy:** Nginx for handling incoming requests and routing.

For a detailed view of the system's architecture, please refer to the `ARCHITECTURE.md` file in the `docs` folder.

## API Documentation

The backend API provides endpoints for authentication, employee management, and employee self-service. Key API functionalities are documented in the `API.md` file within the `docs` folder. Some important aspects include:

* **Authentication:** Handles user login/logout, session management, and password changes. See `/api/auth` endpoints.
* **Employee Management:** Allows creation, retrieval, and modification of employee records. See `/api/employee` endpoints.
* **Employee Self-Service:** Provides endpoints for employees to access their personal information. See `/api/employee/self` endpoints.

## Installation

Instructions for setting up and running a local development environment of the LeanX ERP-System can be found in the `INSTALLATION.md` file in the `docs` folder.

## Database

The system uses MySQL for persistent data storage. Details about the database schema and setup are available in the `DATABASE.md` file within the `docs` folder.

## References

* **API Documentation:** [docs/API.md](docs/API.md)
* **Architecture Overview:** [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)
* **Database Setup:** [docs/DATABASE.md](docs/DATABASE.md)
* **Installation Guide:** [docs/INSTALLATION.md](docs/INSTALLATION.md)

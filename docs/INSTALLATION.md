# Installation Guide for LeanX ERP-System (Development Environment)

This document outlines the steps required to set up a local development environment for the LeanX ERP-System using Docker Compose and a provided deployment script.

## 1. Fork the GitHub Repository

1.  **Navigate to the LeanX ERP-System repository** on GitHub using the following link: [https://github.com/SalesUP-GmbH/leanx-erp-system.git](https://github.com/SalesUP-GmbH/leanx-erp-system.git) in your web browser.
2.  **Click the "Fork" button** located in the top-right corner of the page.
3.  **Choose your personal GitHub account** as the destination for the fork.
4.  Once the forking process is complete, you will be redirected to your own copy of the repository on GitHub (the URL will be `https://github.com/your-username/leanx-erp-system.git`).

## 2. Clone Your Forked Repository

1.  **Open your terminal** or command prompt.
2.  **Navigate to the directory** where you want to store the project on your local machine using the `cd` command.
3.  **Copy the HTTPS clone URL** of your forked repository from your GitHub page (it will look like `https://github.com/your-username/leanx-erp-system.git`).
4.  **Clone the repository** using the `git clone` command, replacing the placeholder URL with the copied URL:

    ```bash
    git clone [https://github.com/your-username/leanx-erp-system.git](https://github.com/your-username/leanx-erp-system.git)
    ```

5.  **Navigate into the cloned repository directory:**

    ```bash
    cd leanx-erp-system
    ```

## 3. Install Docker and Docker Compose

The LeanX ERP-System development environment relies on Docker and Docker Compose for containerization. Follow the instructions below to install them based on your operating system.

### 3.1. Docker Installation

* **Docker Desktop (Recommended for macOS and Windows):**
    1.  Go to the official Docker website: [https://www.docker.com/products/docker-desktop/](https://www.docker.com/products/docker-desktop/)
    2.  Download the Docker Desktop installer for your operating system.
    3.  Follow the installation instructions provided in the installer.
    4.  Once installed, start Docker Desktop.

* **Docker Engine (for Linux distributions):**
    1.  Refer to the official Docker documentation for your specific Linux distribution: [https://docs.docker.com/engine/install/](https://docs.docker.com/engine/install/)
    2.  Follow the instructions to install the Docker Engine and the Docker CLI.
    3.  After installation, you might need to add your user to the `docker` group to run Docker commands without `sudo`.

### 3.2. Docker Compose Installation

* **Docker Desktop (macOS and Windows):** Docker Compose is typically included with Docker Desktop. Verify the installation by running the following command in your terminal:

    ```bash
    docker-compose --version
    ```

    If Docker Compose is installed, you will see its version information.

* **Docker Compose (Linux distributions):**
    1.  Refer to the official Docker Compose installation guide for Linux: [https://docs.docker.com/compose/install/linux/](https://docs.docker.com/compose/install/linux/)
    2.  Follow the instructions to download and install Docker Compose. Ensure it is executable and in your system's PATH.
    3.  Verify the installation by running:

        ```bash
        docker-compose --version
        ```

## 4. Run the Development Deployment Script

The repository includes a bash script to build the backend and deploy the development environment using Docker Compose.

1.  **Ensure you are in the root directory** of your cloned repository in the terminal (`leanx-erp-system`).
2.  **Make the deployment script executable** if it isn't already:

    ```bash
    chmod +x dev/deploy-dev.sh
    ```

3.  **Execute the deployment script:**

    ```bash
    ./dev/deploy-dev.sh
    ```

4.  **Observe the output in the terminal.** The script will perform the following actions:
    * Build the backend Java application using Maven (`mvn clean package`).
    * Navigate to the directory containing the `docker-compose.yml` file (`dev/`).
    * Stop and remove any existing Docker containers related to the LeanX ERP-System.
    * Build or pull the necessary Docker images as defined in `docker-compose.yml`.
    * Create and start the Docker containers in detached mode (`-d`).
    * Wait for a short period (10 seconds) for the services to start.
    * Check if the `leanx-web-server` (Nginx), `leanx-frontend`, `leanx-backend`, and `leanx-db` containers are running.
    * Report the status of each container.
    * Print the URL to access the application.

5.  **Once the script completes without errors, your development environment should be running.**

## 5. Accessing the Development Environment

The deployment script will output the URL to access the application:

# Todo Application

A full-stack todo application designed to help you manage your tasks efficiently. This application leverages a modern technology stack for both its frontend and backend, ensuring a smooth and responsive user experience.

## Technologies Used

This project is built with the following technologies:

### Frontend

* **React 18**: A declarative, component-based JavaScript library for building user interfaces.
* **Tailwind CSS**: A utility-first CSS framework for rapidly styling the application.
* **JavaScript (ES6+)**: The core programming language for frontend development.

### Backend

* **Java 17+**: The robust and widely used programming language for the backend.
* **Spring Boot 3.x**: A powerful framework for building production-ready, stand-alone Spring applications.
* **Spring Data JPA**: Simplifies data access with Spring applications, providing easy integration with relational databases.
* **PostgreSQL**: A powerful, open-source object-relational database system for storing application data.
* **Maven**: A build automation tool used primarily for Java projects.

## Prerequisites

Before you can run this application, please ensure you have the following software installed on your system:

* **Java 17 or higher**
* **Node.js 16 or higher**
* **npm or yarn** (Node.js package manager)
* **PostgreSQL 12 or higher**
* **Maven 3.6 or higher**
* **Docker** and **Docker Compose** (for easy setup and running of services)

## Running the Application

This application can be easily run using Docker Compose, which orchestrates all necessary services (frontend, backend, and database).

1.  **Start all services:**
    ```bash
    docker-compose up -d
    ```
    This command will start all services in detached mode.

2.  **View logs:**
    To monitor the logs of the running services:

    * **All services:**
        ```bash
        docker-compose logs -f
        ```
    * **Specific service (e.g., backend, frontend, database):**
        ```bash
        docker-compose logs -f [service_name]
        # Example: docker-compose logs -f backend
        # Example: docker-compose logs -f frontend
        # Example: docker-compose logs -f database
        ```

3.  **Stop all services:**
    To stop all running services:
    ```bash
    docker-compose down
    ```

4.  **Stop and remove volumes (clears database):**
    If you wish to stop the services and also remove the associated data volumes (which will clear the database):
    ```bash
    docker-compose down -v
    ```

## Access the Application

Once the application services are running, you can access them at the following URLs:

* **Frontend**: `http://localhost:3000`
* **Backend API**: `http://localhost:8080`
* **Database**: `localhost:5432` (accessible from your host machine)

## API Endpoints

The backend API provides the following endpoints for managing tasks:

| Method | Endpoint | Description |
| :----- | :------------------- | :---------------------- |
| `GET` | `/api/v1/tasks` | Get paginated tasks |
| `POST` | `/api/v1/tasks` | Create a new task |
| `PUT` | `/api/v1/tasks/{id}` | Update an existing task |
| `DELETE` | `/api/v1/tasks/{id}` | Delete a task |
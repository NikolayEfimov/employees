# Employee Management System

This is a simple Employee Management System that allows you to create, update, delete, and manage employees.
The application includes a RESTful API built with Spring Boot and a frontend built with React and TypeScript.

## Features

- Create an employee
- Update an employee
- Delete an employee
- Link a supervisor to an employee
- Display a list of employees with all known data
- Handle validation errors
- Ensure no infinite recursion in supervisor assignment
- Cascade delete handling

## Prerequisites

- Java 17
- Maven
- Node.js
- npm (Node Package Manager)

## Backend Setup

1**Build the backend application**:
    ```bash
    cd backend
    mvn clean install
    ```

2**Run the backend application**:
    ```bash
    mvn spring-boot:run
    ```

The backend application will start on `http://localhost:8080`.

## Frontend Setup

1. **Navigate to the frontend directory**:
    ```bash
    cd frontend
    ```

2. **Install the dependencies**:
    ```bash
    npm install
    ```

3. **Start the frontend application**:
    ```bash
    npm start
    ```

The frontend application will start on `http://localhost:3000`.

## What can be improved further

### Improve Test Coverage

- Increase the test coverage by adding more unit tests, integration tests, and end-to-end tests.

### Logging

- Implement logging in both the backend and frontend to capture important events and errors.

### Adding Another Database

- Support multiple databases (e.g., PostgreSQL, MySQL, etc.).

### Security

- Implement authentication and authorization using Spring Security.

### UI/UX Enhancements

- Improve the user interface and user experience of the frontend application.
- Add more user-friendly error messages and loading indicators.

### API Documentation

- Use Swagger/OpenAPI to document the RESTful API.
- Provide interactive API documentation for easy testing and integration.

### Containerization

- Dockerize the application for easy deployment.
- Set up Docker Compose to manage multi-container applications.
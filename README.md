# RideFlow — Cab Booking & Management System

## Project Overview

RideFlow is a cab booking and management system that allows riders to request rides and drivers to view and accept available rides. It uses role-based access control to separate rider and driver functions.

## Technologies Used

* **Frontend:** React, Vite, JavaScript, CSS
* **Backend:** Java, Spring Boot, Spring Security
* **Database:** PostgreSQL
* **Authentication:** JWT and BCrypt password hashing
* **Build Tool:** Maven

## Main Features

* Rider and driver login
* Secure JWT-based authentication
* Cab booking with pickup and destination
* Ride fare calculation
* Rider ride history
* Driver list of available rides
* Driver ride acceptance
* Role-based API access control

## Project Structure

* `frontend/` — React user interface
* `backend/` — Spring Boot REST API
* `admin-panel/` — Admin panel project folder
* `docs/` — Project documentation folder

## Running the Project

### 1. Start PostgreSQL

Ensure PostgreSQL is running and the `rideflow` database exists.

### 2. Configure the backend

Set the environment variables required by `backend/src/main/resources/application.properties`:

* `RIDEFLOW_DB_USERNAME`
* `RIDEFLOW_DB_PASSWORD`
* `RIDEFLOW_JWT_SECRET`

### 3. Start the backend

Open PowerShell in the `backend/` folder and run:

```powershell
.\mvnw.cmd spring-boot:run
```

Backend health endpoint: `http://localhost:8080/api/health`

### 4. Start the frontend

Open a separate terminal in the `frontend/` folder and run:

```powershell
npm install
npm run dev
```

Open the local URL printed by Vite, usually `http://localhost:5173/`.

## GitHub Repository

https://github.com/asifali802302-hub/rideflow

## Project Status

The rider booking, ride history, driver login, available rides, and ride acceptance workflows have been tested locally.

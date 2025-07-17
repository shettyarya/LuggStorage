# LuggStorage - JavaFX Client-Server Luggage Booking System

## Overview

**LuggStorage** is a JavaFX-based client-server application for managing luggage bookings.  
It features user registration, login, booking management, and payment confirmation, all backed by a MySQL database.  
The UI is styled with CSS for a modern look and dynamic user experience.

---

## Features

- User registration with Aadhar and phone validation
- Secure login (client-server communication)
- Book luggage storage with date, days, and bag count
- View previous bookings
- Payment confirmation and booking cancellation
- Modern, dynamic JavaFX UI with custom styles

---

## Project Structure

```
src/
  main/
    java/
      com/
        example/
          login_second/
            Client.java
            Server.java
            BookingDetails.java
            LoginDetails.java
    resources/
      com/
        example/
          login_second/
            style.css
```

---

## Prerequisites

- **Java 11 or higher** (JavaFX modules required)
- **MySQL Server** running locally (default port 3306)
- **MySQL JDBC Driver** (`mysql-connector-java-x.x.x.jar`)
- **JavaFX SDK** (if not using a JDK that bundles JavaFX)
- **Database setup**:
  - Database: `login`
  - Tables: `user`, `aadhar`, `booking_details`
  - Update DB credentials in code if needed (default: user `root`, password `aryashetty`)

---

## Database Schema Example

You should have tables similar to:

```sql
CREATE TABLE user (
    uid INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE,
    password VARCHAR(50),
    firstName VARCHAR(50),
    lastName VARCHAR(50),
    phone VARCHAR(15),
    aadhar VARCHAR(12) UNIQUE
);

CREATE TABLE aadhar (
    aadharNumber VARCHAR(12) PRIMARY KEY
);

CREATE TABLE booking_details (
    bid INT PRIMARY KEY,
    uid INT,
    address VARCHAR(255),
    bookingDate DATE,
    days INT,
    bags INT,
    totalAmount INT,
    paymentStatus INT,
    FOREIGN KEY (uid) REFERENCES user(uid)
);
```

---

## How to Run

### 1. **Start MySQL Server**

Make sure your MySQL server is running and the database/tables are set up.

### 2. **Compile the Code**

Open a terminal in your project root and run:

```sh
javac -cp "src;path\to\mysql-connector-java.jar;path\to\javafx\lib\*" src\main\java\com\example\login_second\*.java
```
- Replace `path\to\mysql-connector-java.jar` with the actual path to your MySQL JDBC driver.
- Replace `path\to\javafx\lib\*` with the path to your JavaFX SDK `lib` directory (if needed).

### 3. **Start the Server**

```sh
java -cp "src;path\to\mysql-connector-java.jar" com.example.login_second.Server
```

### 4. **Start the Client**

In a new terminal:

```sh
java -cp "src;path\to\mysql-connector-java.jar;path\to\javafx\lib\*" --module-path path\to\javafx\lib --add-modules javafx.controls,javafx.fxml com.example.login_second.Client
```

- Adjust the `--module-path` and `--add-modules` options as needed for your JavaFX setup.

### 5. **Using an IDE (Recommended)**

- Import the project as a Maven/Gradle/Java project.
- Add the MySQL JDBC driver and JavaFX libraries to your project dependencies.
- Run `Server.java` first, then `Client.java`.

---

## Notes

- **Always start the server before the client.**
- Update database credentials in the code if your MySQL setup is different.
- The UI is styled via `style.css` in the resources folder.
- For JavaFX setup help, see: [OpenJFX Documentation](https://openjfx.io/)

---

## License

This project is for educational/demo purposes.

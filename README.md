# MinFlash (Backend)

This is the **backend** of a flashcard learning application built with **Spring Boot** and **MySQL**. It handles user authentication, manages flashcard and set data, and implements a **Spaced Repetition System (SRS)** for users.

## Features

- **User Authentication**: JWT-based authentication and OTP email verification.
- **Flashcard & Set Management**: CRUD operations for flashcards and flashcard sets.
- **Folder Management**: Users can organize sets by adding them to relevant folders.
- **Category Management**: Users can select category for their sets and request new category.
- **Admin**: Admin can implement requested categories and delete existing ones.
- **Spaced Repetition System (SRS)**: Tracks and manages the review process for flashcards.
- **Profile Customization**: Manage user profile data including profile image upload.
- **API Documentation**: API routes for interacting with the frontend.

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/AradhyaSharma31/Minflash-Backend.git
   
2. Set up your port, MySQL database and configure the application properties (src/main/resources/application.properties) with your database credentials.
  
3. Build the application:

   ./mvnw clean install

4. run the application

5. The backend will run on:

   http://locahost:{your-port}

## Technologies Used

Java Spring Boot: Backend framework

MySQL: Database for storing user and flashcard data

JWT: Authentication mechanism

Docker: For containerization and easy deployment

Postman: API testing

Azure: For storing user-uploaded images

## Contributing

Feel free to fork and contribute to this project! Here are some ways you can help:

Report bugs or suggest features.

Submit pull requests with fixes or improvements.

Improve the documentation.

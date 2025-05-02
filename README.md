# Bulletin Board Project

A simple bulletin board application built with Spring Boot, allowing users to create, view, modify, and delete posts. This project implements a secure post management system with password protection for editing and deleting posts.

## Features

- View a list of all posts
- Create new posts
- View individual post details
- Edit existing posts (password protected)
- Delete posts (soft delete, password protected)
- Tracks views, creation time, and modification time

## Setup

### Prerequisites

- Java 21 or higher
- Maven
- PostgreSQL database

### Database Configuration

1. Set up a PostgreSQL database
2. Run the schema script located at `/sql/schema.sql`
3. Configure the database connection properties

### Configuration Files

Create or edit the following environment properties files:

#### For Main Application
Create `.env.properties` in the application resources directory `src/main/resources/` with:
```
DATABASE_URL=jdbc:postgresql://localhost:5432/your_database_name
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password
```

#### For Testing
Create `.env.properties` in the test resources directory `src/main/resources/` with:
```
DATABASE_URL=jdbc:postgresql://localhost:5432/your_test_database_name
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password
```

Note: Make sure to use a separate database for testing.

## Running the Application

1. Compile the project:
```bash
mvn clean install
```

2. Run the application:
```bash
mvn spring-boot:run
```

3. Access the application in your browser:
```
http://localhost:8080
```

## Running Tests

1. Ensure the test database is configured in your test `.env.properties` file in the `/src/test/resources/` directory.

2. Run the tests using Maven:

```bash
mvn test
```

The project uses JUnit 5 and Mockito for testing. Tests follow the naming pattern *Test.java and are automatically discovered and executed by the Maven Surefire plugin.

## Using the Bulletin Board

### Viewing Posts
- The homepage displays a list of all posts
- Each post shows: number, title, author, view count, and creation date
- Click on a post title to view its full content and increment the view count

### Creating a Post
1. Click the "Create New Post" button on the posts list page
2. Fill in:
    - Author name (max 10 Korean or 20 English characters)
    - Password (required for future edits/deletion)
    - Post title (max 50 Korean or 100 English characters)
    - Content
3. Click "Create" to create the post

### Viewing a Post
- Shows post details including content
- View count will increment when a post is viewed
- Displays modification time if the post has been edited
- Provides buttons to edit, delete, or return to the list

### Editing a Post
1. View the post you want to edit
2. Click the "Edit Post" button
3. Enter the password used when creating the post
4. Make your changes
5. Click "Update" to update the post

### Deleting a Post
1. View the post you want to delete
2. Click the "Delete Post" button
3. Enter the password used when creating the post
4. Confirm deletion

Note: Deleted posts are not removed from the database but will no longer appear in the list.

## Technical Details

- Framework: Spring Boot
- Database: PostgreSQL
- Templates: Thymeleaf
- Libraries: MyBatis
- Password Hashing: BCrypt (via Spring Security)
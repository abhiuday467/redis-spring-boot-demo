# Spring Boot Demo Project

This is a basic Spring Boot project created with Maven.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/redis/demo/
│   │       ├── DemoApplication.java
│   │       └── controller/
│   │           └── HelloController.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/
        └── com/redis/demo/
            └── DemoApplicationTests.java
```

## Running the Application

### Using Maven
```bash
# Clean and compile
mvn clean compile

# Run the application
mvn spring-boot:run

# Or build and run the JAR
mvn clean package
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

### Using IDE
Run the `DemoApplication.java` class directly from your IDE.

## Testing

Once the application is running, you can test it by visiting:
- http://localhost:8080/ - Should display "Hello, Spring Boot!"

## API Endpoints

- `GET /` - Returns a greeting message

## Dependencies

- Spring Boot Starter Web
- Spring Boot Starter Test (for testing)

## Configuration

The application runs on port 8080 by default. You can modify this in `src/main/resources/application.properties`.

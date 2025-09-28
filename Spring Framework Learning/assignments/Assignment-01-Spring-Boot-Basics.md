# Assignment 1: Spring Boot Basics

## Objective

Create a Spring Boot application that demonstrates your understanding of basic Spring Boot concepts, REST controllers, dependency injection, and configuration management.

## Requirements

Create a **Student Management System** REST API with the following specifications:

### 1. Project Setup
- Create a new Spring Boot project named `student-management`
- Use Java 17 and Spring Boot 3.x
- Include the following dependencies:
  - `spring-boot-starter-web`
  - `spring-boot-starter-actuator`
  - `spring-boot-starter-test`

### 2. Student Model
Create a `Student` class with the following properties:
- `id` (Long)
- `firstName` (String)
- `lastName` (String)
- `email` (String)
- `age` (Integer)
- `course` (String)

### 3. REST Endpoints

Implement the following REST endpoints in a `StudentController`:

#### GET Endpoints
1. `GET /api/students` - Return a list of all students
2. `GET /api/students/{id}` - Return a specific student by ID
3. `GET /api/students/search?course={course}` - Return students enrolled in a specific course
4. `GET /api/students/count` - Return the total number of students

#### POST Endpoints
5. `POST /api/students` - Create a new student (accept JSON body)

#### PUT Endpoints
6. `PUT /api/students/{id}` - Update an existing student

#### DELETE Endpoints
7. `DELETE /api/students/{id}` - Delete a student by ID

### 4. Configuration
- Configure the application to run on port `8090`
- Add custom application properties:
  - `app.name=Student Management System`
  - `app.version=1.0.0`
  - `app.max.students=100`
- Use `@Value` annotation to inject these properties

### 5. Data Storage
- Use an in-memory list or map to store student data
- Pre-populate with at least 3 sample students

### 6. Error Handling
- Handle cases where a student is not found (return appropriate HTTP status)
- Validate input data (e.g., email format, age constraints)

### 7. Actuator
- Enable health, info, and metrics endpoints
- Add custom application info to the `/actuator/info` endpoint

## Bonus Tasks (Optional)

1. Add input validation using Bean Validation annotations
2. Create a custom exception handler using `@ControllerAdvice`
3. Add CORS configuration for frontend integration
4. Create a simple HTML page to test the API endpoints
5. Add logging to track API calls

## Submission Guidelines

### File Structure
```
student-management/
├── pom.xml
├── src/main/java/com/example/student/
│   ├── StudentManagementApplication.java
│   ├── controller/
│   │   └── StudentController.java
│   ├── model/
│   │   └── Student.java
│   └── service/
│       └── StudentService.java
├── src/main/resources/
│   └── application.properties
└── README.md
```

### Documentation Requirements
Create a `README.md` file that includes:
- Project description
- How to run the application
- API endpoint documentation with examples
- Testing instructions using curl commands or Postman

### Testing Requirements
- Test all endpoints using curl, Postman, or browser
- Include screenshots or examples of API responses
- Test error scenarios (invalid IDs, malformed JSON, etc.)

## Evaluation Criteria

| Criteria | Points | Description |
|----------|---------|-------------|
| **Project Setup** | 15 | Correct Spring Boot project structure and dependencies |
| **Student Model** | 10 | Proper model class with required fields |
| **REST Endpoints** | 30 | All required endpoints implemented correctly |
| **Configuration** | 15 | Proper use of application.properties and @Value |
| **Data Management** | 15 | In-memory storage with sample data |
| **Error Handling** | 10 | Appropriate error responses and status codes |
| **Documentation** | 5 | Clear README with API documentation |
| **Bonus Tasks** | 10 | Additional features (optional) |

**Total: 100 points (+10 bonus)**

## Example API Calls

### Create a Student
```bash
curl -X POST http://localhost:8090/api/students \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "age": 22,
    "course": "Computer Science"
  }'
```

### Get All Students
```bash
curl http://localhost:8090/api/students
```

### Search by Course
```bash
curl "http://localhost:8090/api/students/search?course=Computer Science"
```

### Expected Response Format
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "age": 22,
  "course": "Computer Science"
}
```

## Tips for Success

1. **Start Simple**: Begin with basic GET endpoints, then add complexity
2. **Test Frequently**: Test each endpoint as you implement it
3. **Use Proper HTTP Status Codes**: 200 for success, 404 for not found, 400 for bad requests
4. **Follow REST Conventions**: Use appropriate HTTP methods for different operations
5. **Handle Edge Cases**: Consider null values, empty lists, invalid inputs
6. **Use Spring Boot Features**: Leverage auto-configuration and starter dependencies

## Common Pitfalls to Avoid

- Forgetting `@RestController` annotation
- Not handling null/empty inputs
- Using wrong HTTP methods for operations
- Forgetting to configure the port in application.properties
- Not testing the application before submission

## Submission Deadline

Submit your completed assignment within **1 week** of starting this module.

## Help and Resources

- Refer to the example in `examples/01-hello-spring-boot/`
- Check Spring Boot documentation: https://spring.io/projects/spring-boot
- Use Spring Boot guides: https://spring.io/guides
- Ask questions in the discussion forum or during lab sessions

Good luck! 🚀
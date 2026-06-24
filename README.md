# StudyMind Backend

Backend API cho ứng dụng StudyMind — **Spring Boot 3** + **MongoDB**.

## Yêu cầu

- Java 21+
- Maven 3.9+
- MongoDB (local hoặc MongoDB Atlas)

## Chạy ứng dụng

```bash
mvn spring-boot:run
```

Sau khi server start xong, trình duyệt sẽ tự mở Swagger (tắt: `$env:AUTO_OPEN_SWAGGER="false"`).

API: `http://localhost:8080`  
Swagger: `http://localhost:8080/swagger-ui/index.html`

### Biến môi trường

| Biến | Mặc định | Mô tả |
|------|----------|--------|
| `MONGODB_URI` | `mongodb://localhost:27017/studymind` | Connection string MongoDB |
| `SERVER_PORT` | `8080` | Port HTTP |

## Kiểm tra

```bash
curl http://localhost:8080/api/v1/health
```

## Cấu trúc thư mục

```
src/main/java/com/studymind/
├── config/        # Cấu hình (MongoDB auditing, ...)
├── controller/    # REST controllers
├── dto/           # Request/Response DTO
├── exception/     # Exception handling
├── model/         # MongoDB documents
├── repository/    # Spring Data repositories
└── service/       # Business logic
```

## Build

```bash
mvn clean package
java -jar target/studymind-backend-0.0.1-SNAPSHOT.jar
```

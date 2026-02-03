# PlutoCart

# PlutoCart 🛒
A modern e-commerce microservices platform built with Spring Boot and Maven monorepo architecture.

## 🚀 Quick Start

### Prerequisites
Ensure you have the following installed:
- **Java 22** ([Download](https://adoptium.net/))
- **Maven 3.9+** ([Download](https://maven.apache.org/download.cgi))
- **Docker Desktop** ([Download](https://www.docker.com/products/docker-desktop))

Verify installations:

```bash

java -version    # Should show Java 22
mvn -version     # Should show Maven 3.9+
docker --version # Should show Docker 20+
```
---

### ⚡ Option 1: Run Everything with Docker (Fastest)

```bash

# 1. Clone the repository

git clone <repository-url>
cd pluto-cart

# 2. Build all services
mvn clean install -DskipTests

# 3. Start all services with Docker Compose
docker-compose up --build

# All services will be running in ~3-5 minutes!
```

**Service URLs:**

- User Service: http://localhost:8081/health

- Product Service: http://localhost:8082/health

- Cart Service: http://localhost:8083/health

- Order Service: http://localhost:8084/health

- Swagger UI (User Service): http://localhost:8081/swagger-ui.html

---
### 🔧 Option 2: Run Services Locally (Development Mode)
#### Step 1: Start Databases Only
```bash
# Start PostgreSQL and MySQL in Docker
docker-compose -f docker-compose-db.yml up -d

# Verify databases are running
docker ps
```

#### Step 2: Build the Project
```bash
# Build all modules (takes ~1-2 minutes)
mvn clean install -DskipTests
```

#### Step 3: Run Individual Services
Open 4 terminal windows and run:
**Terminal 1 - User Service:**

```bash
cd services/user-service

mvn spring-boot:run
```

**Terminal 2 - Product Service:**

```bash
cd services/product-service

mvn spring-boot:run
```

**Terminal 3 - Cart Service:**

```bash
cd services/cart-service

mvn spring-boot:run
```

**Terminal 4 - Order Service:**

```bash
cd services/order-service

mvn spring-boot:run
```
---

## 📋 Project Structure

Mono-repo Spring Boot project built with Maven.

```
pluto-cart/

├── pom.xml                          # Parent POM (manages all dependencies & versions)

├── docker-compose.yml               # Full stack (databases + services)

├── docker-compose-db.yml            # Databases only

├── common/                          # Shared modules

│   ├── common-utils/                # Utility classes

│   ├── common-security/             # Security configurations

│   └── common-exception/            # Exception handlers

└── services/                        # Microservices

    ├── user-service/                # User management (Port: 8081)

    ├── product-service/             # Product catalog (Port: 8082)

    ├── cart-service/                # Shopping cart (Port: 8083)

    └── order-service/               # Order processing (Port: 8084)

```

## Structure

- parent pom manages shared dependencies

- each module is an independent Spring Boot service

- Contains common utilities and configurations in a shared module
---



## Tech Stack

- Java

- Spring Boot

- Maven

- Docker

- PostgreSQL

- MySQL

## 🛠️ Tech Stack

| Technology | Version | Purpose |

|------------|---------|---------|

| **Java** | 22 | Programming Language |

| **Spring Boot** | 4.0.2 | Application Framework |

| **Maven** | 3.9+ | Build Tool & Dependency Management |

| **PostgreSQL** | 16 | Database (Cart Service) |

| **MySQL** | 8.0 | Database (Order Service) |

| **Docker** | Latest | Containerization |

| **Springdoc OpenAPI** | 2.6.0 | API Documentation |

| **Lombok** | 1.18.36 | Code Generation |

---

## 🎯 Key Features

### Parent POM Management

- ✅ Centralized dependency version management

- ✅ Common dependencies inherited by all services

- ✅ Consistent build configuration across modules

- ✅ BOM (Bill of Materials) pattern support



### Common Modules

- **common-utils**: Shared utility classes and helpers

- **common-security**: Security configurations and filters

- **common-exception**: Global exception handling



### Microservices

- **user-service**: User registration, authentication, profile management

- **product-service**: Product catalog, search, inventory

- **cart-service**: Shopping cart operations (PostgreSQL)

- **order-service**: Order processing, checkout (MySQL)



---



## 🧪 Testing



```bash

# Run all tests
mvn test

# Run tests for specific service
cd services/user-service
mvn test

# Run with coverage
mvn verify
```

---

## 🔍 Health Checks
Each service exposes health endpoints:

```bash
# Check if all services are healthy

curl http://localhost:8081/health  # User Service

curl http://localhost:8082/health  # Product Service

curl http://localhost:8083/health  # Cart Service

curl http://localhost:8084/health  # Order Service

```

Expected response:

```json
{
  "status": "UP"
}
```
---

## 📚 API Documentation

Swagger UI is available for services that have it enabled:

- **User Service**: http://localhost:8081/swagger-ui.html

- **Other Services**: Coming soon...

---

## 🐳 Docker Commands

```bash

# Start all services
docker-compose up -d

# Stop all services
docker-compose down

# View logs
docker-compose logs -f

# View logs for specific service
docker-compose logs -f user-service

# Restart specific service
docker-compose restart cart-service

# Clean up everything (including volumes)
docker-compose down -v
```
---



## 💾 Database Configuration

### PostgreSQL (Cart Service)

- **Host**: localhost

- **Port**: 5432

- **Database**: cart

- **Username**: myuser

- **Password**: mypassword



### MySQL (Order Service)

- **Host**: localhost

- **Port**: 3306

- **Database**: ordersdb

- **Username**: order_user

- **Password**: order_password



---



## 🔧 Troubleshooting



### Port Already in Use

```bash
# Find process using port (e.g., 8081)
lsof -i :8081

# Kill the process
kill -9 <PID>

```



### Docker Issues

```bash

# Clean up Docker
docker system prune -a

# Remove all containers and volumes
docker-compose down -v
```



### Maven Build Issues

```bash
# Clean Maven cache
mvn clean

# Force update dependencies
mvn clean install -U

# Skip tests if needed
mvn clean install -DskipTests

```



### Database Connection Issues

```bash

# Verify databases are running
docker ps

# Check database logs
docker-compose logs postgres-db
docker-compose logs mysql-db

# Restart databases
docker-compose restart postgres-db mysql-db
```
---

## 📝 Development Workflow
### Adding a New Dependency

**Step 1**: Add version to parent `pom.xml`:

```xml

<properties>
    <new-library.version>1.0.0</new-library.version>
</properties>

 

<dependencyManagement>

    <dependencies>

        <dependency>
            <groupId>com.example</groupId>
            <artifactId>new-library</artifactId>
            <version>${new-library.version}</version>
        </dependency>

    </dependencies>

</dependencyManagement>

```



**Step 2**: Use in service without version:

```xml

<dependency>
    <groupId>com.example</groupId>
    <artifactId>new-library</artifactId>
</dependency>

```

### Creating a New Service
1. Copy an existing service directory

2. Update `pom.xml` with new artifact name

3. Update `application.yaml` with new port

4. Add module to parent `pom.xml`:

   ```xml

   <modules>
       <module>services/new-service</module>
   </modules>

   ```

5. Build and run!
---



## 👥 Team
Built with ❤️ by the PlutoCart Team
---

**Happy Coding! 🚀**
# PrimeEstate

A real estate web application built with Java (Servlet/JDBC) backend and HTML/JavaScript/Tailwind CSS frontend.

## Tech Stack

| Layer    | Technology                          |
|----------|-------------------------------------|
| Backend  | Java 17, Jakarta Servlets, JDBC     |
| Frontend | HTML5, JavaScript (Vanilla), Tailwind CSS |
| Database | MySQL — `OOP_Real_state`            |
| Build    | Maven (WAR → Tomcat)                |

## Project Structure

```
OOP-Group_Project/
├── backend/                        # Java Maven project
│   ├── pom.xml
│   └── src/main/java/com/primeestate/
│       ├── config/DBConnection.java
│       ├── model/      User.java, Property.java
│       ├── dao/        UserDAO.java, PropertyDAO.java
│       ├── servlet/    AuthServlet.java, PropertyServlet.java
│       └── util/       JsonResponse.java, CORSFilter.java
├── frontend/
│   ├── index.html                  # Home page
│   ├── pages/
│   │   ├── properties.html         # Listings page
│   │   ├── property-detail.html    # Single property
│   │   ├── login.html
│   │   └── register.html
│   ├── js/
│   │   ├── api.js                  # Fetch wrapper for all API calls
│   │   ├── auth.js                 # Session management
│   │   ├── main.js                 # Homepage logic
│   │   └── properties.js           # Listings + filters + pagination
│   └── css/style.css
└── database/schema.sql             # Full DB schema + sample data
```

## Setup

### 1. Database

```sql
mysql -u root -p < database/schema.sql
```

Or open `database/schema.sql` in phpMyAdmin and execute it.

### 2. Backend

Update the MySQL password in [backend/src/main/java/com/primeestate/config/DBConnection.java](backend/src/main/java/com/primeestate/config/DBConnection.java):

```java
private static final String PASSWORD = "your_mysql_password";
```

Build and deploy the WAR to Tomcat:

```bash
cd backend
mvn clean package
cp target/primeestate.war /path/to/tomcat/webapps/
```

The API will be available at `http://localhost:8080/primeestate/api/`.

### 3. Frontend

Open `frontend/index.html` with **Live Server** (VS Code) or any static file server.

The frontend expects the backend at `http://localhost:8080/primeestate/api` (configured in `js/api.js`).

## API Endpoints

| Method | Endpoint                       | Description         |
|--------|--------------------------------|---------------------|
| POST   | `/api/auth/register`           | Register user       |
| POST   | `/api/auth/login`              | Login               |
| POST   | `/api/auth/logout`             | Logout              |
| GET    | `/api/auth/me`                 | Get current user    |
| GET    | `/api/properties`              | List properties     |
| GET    | `/api/properties/{id}`         | Get single property |
| POST   | `/api/properties`              | Create property     |
| PUT    | `/api/properties/{id}`         | Update property     |
| DELETE | `/api/properties/{id}`         | Delete property     |

### Query Parameters (GET /api/properties)

| Param      | Example      | Description        |
|------------|--------------|--------------------|
| `type`     | `sale`/`rent`| Filter by type     |
| `city`     | `Colombo`    | Filter by city     |
| `category` | `apartment`  | Filter by category |
| `page`     | `1`          | Pagination         |
| `limit`    | `9`          | Items per page     |

## OOP Concepts Used

- **Encapsulation** — Model classes (User, Property) with private fields and getters/setters
- **Abstraction** — DAO layer abstracts all SQL from servlets
- **Singleton** — `DBConnection` uses singleton pattern
- **Separation of Concerns** — Model / DAO / Servlet / Util layers

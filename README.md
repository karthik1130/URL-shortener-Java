# URL Shortener - How to Run

## Prerequisites

- **Java 17 or higher** - [Download Java](https://www.oracle.com/java/technologies/downloads/)
- **Gradle 7.0+** (optional if using wrapper)

## Quick Start

### Option 1: Using Gradle Wrapper (Recommended)

**First, generate the Gradle wrapper** (one-time setup):

If you have Gradle installed:
```bash
gradle wrapper --gradle-version 8.5
```

If you don't have Gradle, download it from [gradle.org](https://gradle.org/install/) or use Option 2 below.

**Then run the project**:

**On Windows:**
```bash
# Build
gradlew.bat build

# Run
gradlew.bat bootRun
```

**On Linux/Mac:**
```bash
# Build
./gradlew build

# Run
./gradlew bootRun
```

**Or use the quick run script (Windows only):**
```bash
run.bat
```

### Option 2: Using System Gradle

If you have Gradle installed on your system:

1. **Build the project**:
   ```bash
   gradle build
   ```

2. **Run the application**:
   ```bash
   gradle bootRun
   ```

### Option 3: Using IDE (IntelliJ IDEA / Eclipse / VS Code)

1. **Import the project** as a Gradle project
2. **Locate** `UrlShortenerApplication.java` in `src/main/java/com/urlshortener/`
3. **Right-click** and select "Run" or "Debug"

## Access the Application

Once the application starts, you'll see:
```
Started UrlShortenerApplication in X.XXX seconds
```

- **Frontend UI**: Open your browser and go to `http://localhost:8080`
- **H2 Database Console**: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:urlshortener`
  - Username: `sa`
  - Password: (leave empty)

## Troubleshooting

### Port 8080 already in use
If port 8080 is occupied, change it in `src/main/resources/application.properties`:
```properties
server.port=8081
```

### Java version issues
Verify your Java version:
```bash
java -version
```
Should show Java 17 or higher.

### Gradle not found
Install Gradle or use the wrapper:
- Download from: https://gradle.org/install/
- Or run `gradle wrapper` to generate wrapper files

## Project Structure

```
Demo/
├── src/main/java/          # Java source code
├── src/main/resources/     # Configuration & static files
│   ├── application.properties
│   └── static/            # Frontend (HTML, CSS, JS)
├── build.gradle           # Gradle build configuration
└── settings.gradle        # Gradle settings
```

## Features

- ✅ Shorten long URLs
- ✅ View all shortened URLs
- ✅ Track click counts
- ✅ Copy shortened URLs
- ✅ Modern, responsive UI

# Testing Setup Documentation

## Overview

The MeditationCenter project uses **Testcontainers** for integration testing with PostgreSQL. This ensures tests run against a real database both locally and in CI/CD environments.

---

## Why Testcontainers?

### Problems It Solves

❌ **Without Testcontainers:**
- Tests fail locally if PostgreSQL not running
- Different database credentials for dev/test/CI
- Manual database setup required
- Tests might pass locally but fail in CI
- Can't test against real PostgreSQL features

✅ **With Testcontainers:**
- Tests spin up PostgreSQL automatically
- No local database setup needed
- Same database version as production
- Isolated test environment
- Works identically locally and in CI

---

## How It Works

### Local Development

```
./gradlew test
       ↓
Testcontainers starts PostgreSQL 15 in Docker
       ↓
Flyway runs migrations
       ↓
Tests execute against real PostgreSQL
       ↓
Container automatically stops after tests
```

### GitHub Actions CI

```
CI workflow runs
       ↓
Docker available in CI environment
       ↓
Testcontainers starts PostgreSQL 15
       ↓
Same flow as local development
       ↓
Tests pass consistently
```

---

## Configuration Files

### BaseIntegrationTest.java

**Location:** `src/test/java/com/isipathana/meditationcenter/BaseIntegrationTest.java`

**Purpose:** Base class for all integration tests that need a database

**Key Features:**
- `@Testcontainers` - Enables Testcontainers support
- `@Container` - Defines PostgreSQL container
- `@DynamicPropertySource` - Overrides Spring datasource properties at runtime

**Usage:**
```java
class YourTest extends BaseIntegrationTest {
    @Test
    void yourTest() {
        // Test code here - database automatically available
    }
}
```

---

### application-test.properties

**Location:** `src/test/resources/application-test.properties`

**Purpose:** Test-specific Spring configuration (overridden by Testcontainers)

**Key Settings:**
- Flyway enabled for test database migrations
- Debug logging for troubleshooting
- Properties overridden by `@DynamicPropertySource` in BaseIntegrationTest

---

### build.gradle

**Dependencies Added:**
```gradle
testImplementation 'org.testcontainers:postgresql:1.19.3'
testImplementation 'org.testcontainers:junit-jupiter:1.19.3'
```

---

## Requirements

### Local Development

1. **Docker Desktop** must be installed and running
   - Download: https://www.docker.com/products/docker-desktop

2. **Java 24** (already configured)

3. **No PostgreSQL installation required**
   - Testcontainers handles it

### GitHub Actions CI

- Docker automatically available ✅
- PostgreSQL service container for build process ✅
- Testcontainers works out of the box ✅

---

## Running Tests

### Run All Tests
```bash
./gradlew test
```

### Run Specific Test
```bash
./gradlew test --tests "MeditationCenterApplicationTests"
```

### Run Tests with Debug Logging
```bash
./gradlew test --info
```

### Clean and Test
```bash
./gradlew clean test
```

---

## Test Structure

### Integration Tests (with Database)

**Extend:** `BaseIntegrationTest`

**Example:**
```java
class UserRepositoryTest extends BaseIntegrationTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByEmail() {
        // Test uses real PostgreSQL from Testcontainers
        User user = userRepository.findByEmail("test@example.com");
        assertNotNull(user);
    }
}
```

### Unit Tests (no Database)

**No need to extend** BaseIntegrationTest

**Example:**
```java
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldValidateEmail() {
        // Pure unit test - no database needed
        boolean valid = userService.isValidEmail("test@example.com");
        assertTrue(valid);
    }
}
```

---

## Troubleshooting

### Error: "Docker not running"

**Symptom:**
```
Could not find a valid Docker environment
```

**Solution:**
1. Start Docker Desktop
2. Verify with: `docker ps`
3. Run tests again

---

### Error: "Port 5432 already in use"

**Symptom:**
```
Port 5432 is already allocated
```

**Solution:**
1. Testcontainers uses random ports automatically
2. If you see this, your local PostgreSQL might be interfering
3. Stop local PostgreSQL: `brew services stop postgresql` (macOS)
4. Or change local PostgreSQL port in `application.properties`

---

### Tests Slow on First Run

**Expected Behavior:**
- First run downloads PostgreSQL 15 Docker image (~100MB)
- Subsequent runs reuse cached image
- Startup time: ~2-5 seconds per test class

**Speed Up:**
```bash
# Pre-download image
docker pull postgres:15
```

---

### Error: "password authentication failed for user test"

**Symptom:**
```
FATAL: password authentication failed for user "test"
```

**This means:**
- Test is NOT using Testcontainers
- Trying to connect to your local PostgreSQL
- Your local PostgreSQL doesn't have user "test"

**Solution:**
- Verify test extends `BaseIntegrationTest`
- Check Docker is running
- Run `./gradlew clean test`

---

## CI/CD Configuration

### GitHub Actions Workflow

**File:** `.github/workflows/build.yml`

**Key Configuration:**

1. **PostgreSQL Service** (for build process):
```yaml
services:
  postgres:
    image: postgres:15
    env:
      POSTGRES_DB: meditation_db
      POSTGRES_USER: test
      POSTGRES_PASSWORD: test
```

2. **Docker Setup** (for Testcontainers):
```yaml
- name: Set up Docker Buildx
  uses: docker/setup-buildx-action@v3

- name: Start Docker
  run: |
    sudo systemctl start docker
    sudo chmod 666 /var/run/docker.sock
```

3. **Environment Variables**:
```yaml
env:
  SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/meditation_db
  SPRING_DATASOURCE_USERNAME: test
  SPRING_DATASOURCE_PASSWORD: test
```

---

## Benefits Summary

### For Developers

✅ No local database setup
✅ Tests work immediately after clone
✅ Consistent test environment
✅ Test against production database version
✅ Isolated tests (no data pollution)

### For CI/CD

✅ Reliable test execution
✅ No flaky tests due to environment differences
✅ Fast feedback (parallel test execution)
✅ Same behavior as local development

### For Production

✅ Confidence in database migrations (tested with Flyway)
✅ Real PostgreSQL features tested
✅ No surprises in production

---

## Future Enhancements

### Parallel Test Execution

Currently tests use a shared Testcontainers instance. For faster tests:
```java
@Container
PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
    .withReuse(true); // Reuse container across test classes
```

### Test Data Management

Consider adding:
- Test fixtures with `@Sql` annotations
- Database seeding utilities
- Test data builders

### Performance Monitoring

Add test execution time tracking:
```bash
./gradlew test --profile
```

---

## Comparison: Local vs CI

| Aspect | Local Development | GitHub Actions CI |
|--------|------------------|-------------------|
| PostgreSQL Source | Testcontainers | PostgreSQL Service + Testcontainers |
| Docker | Docker Desktop | GitHub Actions Docker |
| Database User | test (Testcontainers) | test (configured) |
| Speed | Fast (cached image) | Slightly slower (image download) |
| Reliability | High | High |
| Configuration | None needed | Workflow YAML |

---

## Additional Resources

- [Testcontainers Documentation](https://www.testcontainers.org/)
- [Spring Boot Testing Best Practices](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [PostgreSQL Docker Hub](https://hub.docker.com/_/postgres)

---

## Summary

✅ **Tests work locally** - No PostgreSQL installation required
✅ **Tests work in CI** - Identical behavior
✅ **Real database testing** - Not mocked, not H2
✅ **Zero configuration** - Just extend BaseIntegrationTest
✅ **Fast execution** - Container reuse and caching

**Next Steps:**
1. Ensure Docker Desktop is running
2. Run `./gradlew test`
3. Create your test classes extending `BaseIntegrationTest`

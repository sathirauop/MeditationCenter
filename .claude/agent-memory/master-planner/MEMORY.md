# Master Planner Memory

## Project Architecture
- Spring Boot 3.5.5, Java 24, jOOQ (no JPA/Hibernate), PostgreSQL, Lombok
- 7-component per-endpoint pattern: DataAccess (interface), Repository (jOOQ impl), Request (input DTO), Response (output DTO), ResponseBuilder (interface), Presenter (@Component), UseCase (@Service @Transactional)
- Package structure: `rest/{scope}/{feature}/{httpMethod}/`
- Controller is shared per feature at: `rest/{scope}/{feature}/{Feature}Controller.java`
- Constants in `EndPoints.java` nested class structure: `EndPoints.Admin.User.GET_ALL`
- Pagination uses `OffsetSearchResponse<T>` with `OffsetSearchResponse.Factory` injected into Presenters
- Security: method-level `@PreAuthorize` on controller methods, route-level in `SecurityConfig`
- Admin routes already covered by catch-all `.requestMatchers(EndPoints.API + "/**").authenticated()`
- Domain records in `records/{entity}/` - use Lombok `@Builder`
- Response DTOs use `@JsonProperty` for snake_case, implement `ApiResponse`
- getSingle endpoints use folder name `getById` (not `getSingle` for user)
- `ResourceNotFoundException` for 404 errors
- jOOQ generated tables in `com.isipathana.meditationcenter.jooq.Tables`

## Key Files
- `/src/main/java/.../constants/EndPoints.java` - all route constants
- `/src/main/java/.../config/SecurityConfig.java` - security filter chain
- `/src/main/java/.../models/response/OffsetSearchResponse.java` - pagination wrapper
- `/src/main/java/.../models/response/ApiResponse.java` - marker interface for responses

## Database
- Users table: user_id, email, password, name, mobile_number, role, avatar_image_key, is_active, email_verified, created_at, updated_at
- jOOQ table reference: `USERS` from `Tables.USERS`
- Related tables: `BOOKING`, `DONATION` (for user statistics)

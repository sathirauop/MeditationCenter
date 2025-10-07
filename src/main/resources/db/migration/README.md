# Database Migrations

This directory contains Flyway migration scripts for the Meditation Center database.

## Migration Files

Migrations are executed in version order:

1. **V1__create_users_table.sql** - User accounts with authentication and roles
2. **V2__create_meditation_program_table.sql** - Meditation programs offered
3. **V3__create_pricing_table.sql** - Dynamic pricing rules with date validity
4. **V4__create_booking_table.sql** - Program bookings with status tracking
5. **V5__create_payment_table.sql** - Payment transactions for bookings
6. **V6__create_donation_campaign_table.sql** - Donation campaigns/fundraisers
7. **V7__create_donation_table.sql** - Individual donation transactions
8. **V8__create_events_table.sql** - Special events and ceremonies
9. **V9__create_event_registration_table.sql** - User event registrations
10. **V10__create_activity_table.sql** - Reusable activity definitions
11. **V11__create_schedule_template_table.sql** - Reusable schedule templates
12. **V12__create_template_schedule_activity_table.sql** - Activities within templates
13. **V13__create_schedule_override_table.sql** - Date-specific schedule overrides

## Key Design Decisions

### Naming Conventions
- All table names: `snake_case`
- All column names: `snake_case`
- Foreign keys: `{referenced_table}_id`
- Primary keys: `{table_name}_id`

### Standard Columns
Most tables include:
- `created_at` - Record creation timestamp
- `updated_at` - Last modification timestamp
- `is_active` - Soft delete/active status flag

### Data Integrity
- Foreign keys with appropriate `ON DELETE` actions
- Check constraints for data validation
- Unique constraints to prevent duplicates
- Indexes on commonly queried columns

### Enumerations
The following columns use VARCHAR for enum-like values:

**User Roles:**
- USER, ADMIN, INSTRUCTOR

**Booking Types:**
- DAILY, WEEKLY, MONTHLY, EVENT, RETREAT

**Booking Status:**
- PENDING, CONFIRMED, CANCELLED, COMPLETED

**Payment Status:**
- PENDING, COMPLETED, FAILED, REFUNDED

**Payment Methods:**
- CASH, CARD, BANK_TRANSFER, ONLINE

**Event Registration Status:**
- REGISTERED, ATTENDED, CANCELLED, NO_SHOW

## Running Migrations

Migrations run automatically when the application starts with Flyway enabled.

### Manual Migration Commands

```bash
# Run all pending migrations
cd MeditationCenter
./gradlew flywayMigrate

# Get migration status
./gradlew flywayInfo

# Validate migrations
./gradlew flywayValidate

# Clean database (CAUTION: Drops all objects)
./gradlew flywayClean
```

### Creating New Migrations

1. Create a new file: `V{next_version}__{description}.sql`
2. Version must be sequential (e.g., V14, V15, etc.)
3. Use double underscore `__` after version
4. Use descriptive, lowercase names with underscores

Example:
```
V14__add_user_profile_fields.sql
V15__create_instructor_certifications_table.sql
```

## Important Notes

- **Never modify existing migrations** that have been applied to production
- Migrations are run in a transaction (PostgreSQL)
- Failed migrations must be fixed before new ones can run
- Use `baseline-on-migrate=true` for existing databases
- Always test migrations on development database first

## Database Reset (Development Only)

To reset the database during development:

```bash
# Stop the application
# Drop and recreate database in docker
docker-compose down
docker-compose up -d

# Migrations will run automatically on next app start
./gradlew bootRun
```

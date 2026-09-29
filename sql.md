# Database migration index

Database schema and seed data are managed by Flyway. Do not execute old standalone SQL manually.

## Versioned schema migrations

Located in `src/main/resources/db/migration/`:

- `V1`–`V8`: mall schema and initial mall data (existing migrations; do not edit after they have been applied).
- `V9__create_system_schema.sql`: legacy RBAC/system tables, created non-destructively with `CREATE TABLE IF NOT EXISTS`.
- `V10__create_blog_schema.sql`: blog tables, created non-destructively with `CREATE TABLE IF NOT EXISTS`.

## Local demo seed data

`src/main/resources/db/local/V100__seed_local_demo_data.sql` contains local-only sample departments, roles, menus, demo accounts, blog categories, and tags. `application-local.yml` includes this location; other profiles should not load it.

The local migration uses `INSERT IGNORE` to avoid duplicate-key failures when the legacy sample data already exists. It never drops or recreates tables. Review demo accounts and credentials before using a local database outside development.

## Existing databases

The local profile enables Flyway with `baseline-on-migrate` and baseline version `0` so an existing `java_demo` schema can be adopted while Flyway applies the versioned migrations. Before enabling this on a database with important data, take a backup and verify its schema; `CREATE TABLE IF NOT EXISTS` will not alter or validate the structure of tables that already exist.

For future changes, add a new `V{next}__description.sql` migration. Never edit a migration that has already been applied to a shared database; add a forward-only migration instead.

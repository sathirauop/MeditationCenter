package com.isipathana.meditationcenter.wrappers;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.UpdatableRecordImpl;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Test helper wrapper for jOOQ DSLContext.
 * Provides convenient methods for seeding test data and asserting database state.
 *
 * @author Sathira Basnayake
 */
public final class TestDSLContextWrapper {

    private final DSLContext dslContext;

    public TestDSLContextWrapper(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    /**
     * Seed test data into a table.
     * Purges existing data before inserting.
     */
    @SafeVarargs
    public final <T extends org.jooq.Record, R extends UpdatableRecordImpl<R>> void seedData(
            Table<T> table, R... data) {
        seedData(table, Arrays.asList(data));
    }

    /**
     * Seed test data into a table from a list.
     * Purges existing data before inserting.
     */
    public <T extends org.jooq.Record, R extends UpdatableRecordImpl<R>> void seedData(
            Table<T> table, List<R> data) {
        if (data.isEmpty()) return;

        try (ForeignKeyCheckDisabler ignored = new ForeignKeyCheckDisabler(dslContext)) {
            purgeData(table);
            data.forEach(dslContext::executeInsert);
        }
    }

    /**
     * Purge all data from specified tables.
     */
    @SafeVarargs
    public final <T extends org.jooq.Record> void purgeData(Table<T>... tables) {
        try (ForeignKeyCheckDisabler ignored = new ForeignKeyCheckDisabler(dslContext)) {
            for (Table<T> table : tables) {
                dslContext.deleteFrom(table).where(DSL.trueCondition()).execute();
            }
        }
    }

    /**
     * Assert that a record exists in the database with given criteria (AND condition).
     */
    public void seeInDatabase(Table<?> table, Map<String, Object> data) {
        assert dslContext.fetchExists(table, build(table, data, List.of(), Operator.AND));
    }

    /**
     * Assert that a record exists with additional conditions (AND).
     */
    public void seeInDatabase(Table<?> table, Map<String, Object> data, Condition... conditions) {
        assert dslContext.fetchExists(
                table, build(table, data, Arrays.asList(conditions), Operator.AND));
    }

    /**
     * Assert that a record does NOT exist in the database (OR condition).
     */
    public void notSeeInDatabase(Table<?> table, Map<String, Object> data) {
        assert !dslContext.fetchExists(table, build(table, data, List.of(), Operator.OR));
    }

    /**
     * Assert that a record does NOT exist with AND condition.
     */
    public void notSeeInDatabaseWithAndCondition(Table<?> table, Map<String, Object> data) {
        assert !dslContext.fetchExists(table, build(table, data, List.of(), Operator.AND));
    }

    /**
     * Set auto-increment value for a table's primary key.
     */
    public void setAutoIncrement(Table<?> table, int value) {
        dslContext.execute(
                "ALTER SEQUENCE " + table.getName() + "_" + table.getPrimaryKey().getFields().get(0).getName() +
                        "_seq RESTART WITH " + value
        );
    }

    /**
     * Fetch a single field value from the most recently created record.
     */
    public <T> T fetchField(Table<?> table, Field<T> field) {
        return dslContext
                .select(field)
                .from(table)
                .orderBy(DSL.field("created_at").desc())
                .limit(1)
                .fetchOne(field);
    }

    /**
     * Build a jOOQ condition from a map of field-value pairs.
     */
    private Condition build(
            Table<?> table, Map<String, Object> data, List<Condition> conditions, Operator operator) {
        List<Condition> generated = data.entrySet().stream()
                .map(entry -> {
                    var field = Objects.requireNonNull(table.field(entry.getKey()));
                    return resolve(field, entry.getValue());
                })
                .toList();

        List<Condition> merged = new ArrayList<>(generated);
        merged.addAll(conditions);

        return DSL.condition(operator, merged);
    }

    /**
     * Resolve a condition for a field-value pair.
     */
    private Condition resolve(Field<?> field, Object value) {
        if (value instanceof Collection<?> collection) {
            var strings = collection.stream().map(Object::toString).collect(Collectors.joining(","));
            @SuppressWarnings("unchecked")
            Field<String> fieldString = (Field<String>) field;
            return fieldString.contains(strings);
        } else if (value == null) {
            return field.isNull();
        } else {
            return DSL.condition(Map.of(field, value));
        }
    }

    /**
     * Helper class to temporarily disable foreign key checks.
     * Automatically re-enables on close.
     */
    private static class ForeignKeyCheckDisabler implements AutoCloseable {
        private final DSLContext dslContext;

        public ForeignKeyCheckDisabler(DSLContext dslContext) {
            this.dslContext = dslContext;
            // PostgreSQL doesn't use SET FOREIGN_KEY_CHECKS like MySQL
            // We can use SET CONSTRAINTS if needed, but usually not necessary for tests
        }

        @Override
        public void close() {
            // PostgreSQL re-enables constraints automatically at transaction end
        }
    }
}

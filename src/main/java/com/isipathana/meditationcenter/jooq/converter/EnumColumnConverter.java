package com.isipathana.meditationcenter.jooq.converter;

import org.jooq.Converter;

/**
 * Generic converter for converting database VARCHAR columns to Java enums.
 *
 * @param <T> The database type (String)
 * @param <U> The Java enum type
 */
public class EnumColumnConverter<T, U extends Enum<U>> implements Converter<T, U> {

    private final Class<T> databaseType;
    private final Class<U> javaType;

    public EnumColumnConverter(Class<T> databaseType, Class<U> javaType) {
        this.databaseType = databaseType;
        this.javaType = javaType;
    }

    @Override
    public U from(T databaseObject) {
        if (databaseObject == null) {
            return null;
        }
        return Enum.valueOf(javaType, databaseObject.toString());
    }

    @Override
    public T to(U userObject) {
        if (userObject == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        T result = (T) userObject.name();
        return result;
    }

    @Override
    public Class<T> fromType() {
        return databaseType;
    }

    @Override
    public Class<U> toType() {
        return javaType;
    }
}

package com.isipathana.meditationcenter.rest.admin.user.patch;

import com.isipathana.meditationcenter.records.user.User;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.isipathana.meditationcenter.jooq.Tables.USERS;

/**
 * Repository implementation for updating users.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class PatchUserRepository implements PatchUserDataAccess {

    private final DSLContext dslContext;

    @Override
    public Optional<User> findById(Long userId) {
        var record = dslContext
                .selectFrom(USERS)
                .where(USERS.USER_ID.eq(userId))
                .fetchOne();

        if (record == null) {
            return Optional.empty();
        }

        return Optional.of(User.builder()
                .userId(record.get(USERS.USER_ID))
                .email(record.get(USERS.EMAIL))
                .password(record.get(USERS.PASSWORD))
                .name(record.get(USERS.NAME))
                .mobileNumber(record.get(USERS.MOBILE_NUMBER))
                .role(record.get(USERS.ROLE))
                .isActive(record.get(USERS.IS_ACTIVE))
                .emailVerified(record.get(USERS.EMAIL_VERIFIED))
                .createdAt(record.get(USERS.CREATED_AT))
                .updatedAt(record.get(USERS.UPDATED_AT))
                .build());
    }

    @Override
    public boolean existsByEmailAndNotUserId(String email, Long userId) {
        return dslContext.fetchExists(
                dslContext.selectOne()
                        .from(USERS)
                        .where(USERS.EMAIL.eq(email)
                                .and(USERS.USER_ID.ne(userId)))
        );
    }

    @Override
    public User updateUser(Long userId, User user) {
        // Build dynamic update query - only update non-null fields
        var update = dslContext.update(USERS);

        // Start with first non-null field
        var query = user.email() != null ? update.set(USERS.EMAIL, user.email()) : null;

        if (user.password() != null) {
            query = query != null
                    ? query.set(USERS.PASSWORD, user.password())
                    : update.set(USERS.PASSWORD, user.password());
        }

        if (user.name() != null) {
            query = query != null
                    ? query.set(USERS.NAME, user.name())
                    : update.set(USERS.NAME, user.name());
        }

        if (user.mobileNumber() != null) {
            query = query != null
                    ? query.set(USERS.MOBILE_NUMBER, user.mobileNumber())
                    : update.set(USERS.MOBILE_NUMBER, user.mobileNumber());
        }

        // Execute update and fetch result
        var record = query
                .where(USERS.USER_ID.eq(userId))
                .returning(
                        USERS.USER_ID,
                        USERS.EMAIL,
                        USERS.NAME,
                        USERS.MOBILE_NUMBER,
                        USERS.ROLE,
                        USERS.IS_ACTIVE,
                        USERS.EMAIL_VERIFIED,
                        USERS.CREATED_AT,
                        USERS.UPDATED_AT
                )
                .fetchOne();

        return User.builder()
                .userId(record.get(USERS.USER_ID))
                .email(record.get(USERS.EMAIL))
                .name(record.get(USERS.NAME))
                .mobileNumber(record.get(USERS.MOBILE_NUMBER))
                .role(record.get(USERS.ROLE))
                .isActive(record.get(USERS.IS_ACTIVE))
                .emailVerified(record.get(USERS.EMAIL_VERIFIED))
                .createdAt(record.get(USERS.CREATED_AT))
                .updatedAt(record.get(USERS.UPDATED_AT))
                .build();
    }
}

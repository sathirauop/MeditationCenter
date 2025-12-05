package com.isipathana.meditationcenter.repository;

import com.isipathana.meditationcenter.records.user.User;
import com.isipathana.meditationcenter.records.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.isipathana.meditationcenter.jooq.Tables.USERS;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final DSLContext dslContext;

    /**
     * Find user by ID
     */
    public User findById(Long userId) {
        return dslContext
            .select(
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
            .from(USERS)
            .where(USERS.USER_ID.eq(userId))
            .fetchOne(record ->
                User.builder()
                    .userId(record.get(USERS.USER_ID))
                    .email(record.get(USERS.EMAIL))
                    .name(record.get(USERS.NAME))
                    .mobileNumber(record.get(USERS.MOBILE_NUMBER))
                    .role(record.get(USERS.ROLE))
                    .isActive(record.get(USERS.IS_ACTIVE))
                    .emailVerified(record.get(USERS.EMAIL_VERIFIED))
                    .createdAt(record.get(USERS.CREATED_AT))
                    .updatedAt(record.get(USERS.UPDATED_AT))
                    .build()
            );
    }

    /**
     * Find user by email
     */
    public User findByEmail(String email) {
        return dslContext
            .select(
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
            .from(USERS)
            .where(USERS.EMAIL.eq(email))
            .and(USERS.IS_ACTIVE.eq(true))
            .fetchOne(record ->
                User.builder()
                    .userId(record.get(USERS.USER_ID))
                    .email(record.get(USERS.EMAIL))
                    .name(record.get(USERS.NAME))
                    .mobileNumber(record.get(USERS.MOBILE_NUMBER))
                    .role(record.get(USERS.ROLE))
                    .isActive(record.get(USERS.IS_ACTIVE))
                    .emailVerified(record.get(USERS.EMAIL_VERIFIED))
                    .createdAt(record.get(USERS.CREATED_AT))
                    .updatedAt(record.get(USERS.UPDATED_AT))
                    .build()
            );
    }

    /**
     * Find user by email with password (for authentication)
     */
    public User findByEmailWithPassword(String email) {
        return dslContext
            .select(
                USERS.USER_ID,
                USERS.EMAIL,
                USERS.PASSWORD,
                USERS.NAME,
                USERS.MOBILE_NUMBER,
                USERS.ROLE,
                USERS.IS_ACTIVE,
                USERS.EMAIL_VERIFIED,
                USERS.CREATED_AT,
                USERS.UPDATED_AT
            )
            .from(USERS)
            .where(USERS.EMAIL.eq(email))
            .fetchOne(record ->
                User.builder()
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
                    .build()
            );
    }

    /**
     * Create new user
     */
    public User create(User user) {
        var record = dslContext
            .insertInto(USERS)
            .set(USERS.EMAIL, user.email())
            .set(USERS.PASSWORD, user.password())
            .set(USERS.NAME, user.name())
            .set(USERS.MOBILE_NUMBER, user.mobileNumber())
            .set(USERS.ROLE, user.role())
            .set(USERS.IS_ACTIVE, user.isActive() != null ? user.isActive() : true)
            .set(USERS.EMAIL_VERIFIED, user.emailVerified() != null ? user.emailVerified() : false)
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

    /**
     * Find all users by role
     */
    public List<User> findByRole(UserRole role) {
        return dslContext
            .select(
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
            .from(USERS)
            .where(USERS.ROLE.eq(role))
            .and(USERS.IS_ACTIVE.eq(true))
            .orderBy(USERS.CREATED_AT.desc())
            .fetch(record ->
                User.builder()
                    .userId(record.get(USERS.USER_ID))
                    .email(record.get(USERS.EMAIL))
                    .name(record.get(USERS.NAME))
                    .mobileNumber(record.get(USERS.MOBILE_NUMBER))
                    .role(record.get(USERS.ROLE))
                    .isActive(record.get(USERS.IS_ACTIVE))
                    .emailVerified(record.get(USERS.EMAIL_VERIFIED))
                    .createdAt(record.get(USERS.CREATED_AT))
                    .updatedAt(record.get(USERS.UPDATED_AT))
                    .build()
            );
    }

    /**
     * Find all active users
     */
    public List<User> findAllActive() {
        return dslContext
            .select(
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
            .from(USERS)
            .where(USERS.IS_ACTIVE.eq(true))
            .orderBy(USERS.NAME.asc())
            .fetch(record ->
                User.builder()
                    .userId(record.get(USERS.USER_ID))
                    .email(record.get(USERS.EMAIL))
                    .name(record.get(USERS.NAME))
                    .mobileNumber(record.get(USERS.MOBILE_NUMBER))
                    .role(record.get(USERS.ROLE))
                    .isActive(record.get(USERS.IS_ACTIVE))
                    .emailVerified(record.get(USERS.EMAIL_VERIFIED))
                    .createdAt(record.get(USERS.CREATED_AT))
                    .updatedAt(record.get(USERS.UPDATED_AT))
                    .build()
            );
    }

    /**
     * Check if email exists
     */
    public boolean existsByEmail(String email) {
        return dslContext.fetchExists(
            dslContext.selectOne()
                .from(USERS)
                .where(USERS.EMAIL.eq(email))
        );
    }
}

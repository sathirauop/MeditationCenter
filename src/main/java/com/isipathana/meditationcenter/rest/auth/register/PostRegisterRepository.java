package com.isipathana.meditationcenter.rest.auth.register;

import com.isipathana.meditationcenter.records.user.User;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static com.isipathana.meditationcenter.jooq.Tables.USERS;

/**
 * Repository implementation for user registration.
 */
@Repository
@RequiredArgsConstructor
public class PostRegisterRepository implements PostRegisterDataAccess {

    private final DSLContext dslContext;

    @Override
    public boolean existsByEmail(String email) {
        return dslContext.fetchExists(
                dslContext.selectOne()
                        .from(USERS)
                        .where(USERS.EMAIL.eq(email))
        );
    }

    @Override
    public User createUser(User user) {
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
}

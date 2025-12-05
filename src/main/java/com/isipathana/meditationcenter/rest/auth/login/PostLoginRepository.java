package com.isipathana.meditationcenter.rest.auth.login;

import com.isipathana.meditationcenter.records.user.User;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static com.isipathana.meditationcenter.jooq.Tables.USERS;

/**
 * Repository implementation for user login.
 */
@Repository
@RequiredArgsConstructor
public class PostLoginRepository implements PostLoginDataAccess {

    private final DSLContext dslContext;

    @Override
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
}

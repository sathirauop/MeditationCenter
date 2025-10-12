package com.isipathana.meditationcenter.rest.auth.refresh;

import com.isipathana.meditationcenter.records.user.User;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static com.isipathana.meditationcenter.jooq.Tables.USERS;

/**
 * Repository implementation for token refresh.
 */
@Repository
@RequiredArgsConstructor
public class PostRefreshRepository implements PostRefreshDataAccess {

    private final DSLContext dslContext;

    @Override
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
}

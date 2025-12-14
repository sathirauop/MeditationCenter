package com.isipathana.meditationcenter.rest.admin.user.get;

import com.isipathana.meditationcenter.records.user.UserRole;
import com.isipathana.meditationcenter.records.user.User;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

import static com.isipathana.meditationcenter.jooq.Tables.USERS;

/**
 * Repository implementation for fetching users with filtering and pagination.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class GetUsersRepository implements GetUsersDataAccess {

    private final DSLContext dslContext;

    @Override
    public Stream<User> findUsers(Integer limit, Integer offset, UserRole role, Boolean isActive, String search) {
        // Build dynamic WHERE conditions
        Condition condition = buildWhereCondition(role, isActive, search);

        var query = condition != null
                ? dslContext.selectFrom(USERS).where(condition)
                : dslContext.selectFrom(USERS);

        return query
                .orderBy(USERS.CREATED_AT.desc())
                .limit(limit)
                .offset(offset)
                .fetchStream()
                .map(record -> User.builder()
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
    public Long countUsers(UserRole role, Boolean isActive, String search) {
        // Build dynamic WHERE conditions
        Condition condition = buildWhereCondition(role, isActive, search);

        var query = condition != null
                ? dslContext.selectCount().from(USERS).where(condition)
                : dslContext.selectCount().from(USERS);

        return query.fetchOne(0, Long.class);
    }

    /**
     * Build WHERE condition based on filters.
     */
    private Condition buildWhereCondition(UserRole role, Boolean isActive, String search) {
        Condition condition = null;

        // Filter by role
        if (role != null) {
            condition = USERS.ROLE.eq(role);
        }

        // Filter by active status
        if (isActive != null) {
            Condition activeCondition = USERS.IS_ACTIVE.eq(isActive);
            condition = condition != null ? condition.and(activeCondition) : activeCondition;
        }

        // Search by name or email (case-insensitive)
        if (search != null && !search.isBlank()) {
            String searchPattern = "%" + search.toLowerCase() + "%";
            Condition searchCondition = org.jooq.impl.DSL.lower(USERS.NAME).like(searchPattern)
                    .or(org.jooq.impl.DSL.lower(USERS.EMAIL).like(searchPattern));
            condition = condition != null ? condition.and(searchCondition) : searchCondition;
        }

        return condition;
    }
}

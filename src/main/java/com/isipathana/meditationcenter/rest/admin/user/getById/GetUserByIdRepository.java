package com.isipathana.meditationcenter.rest.admin.user.getById;

import com.isipathana.meditationcenter.records.user.UserRole;
import com.isipathana.meditationcenter.records.user.User;
import com.isipathana.meditationcenter.records.user.UserStatistics;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

import static com.isipathana.meditationcenter.jooq.Tables.*;

/**
 * Repository implementation for fetching user details with statistics.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class GetUserByIdRepository implements GetUserByIdDataAccess {

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
    public UserStatistics getUserStatistics(Long userId) {
        // Count total bookings
        Integer totalBookings = dslContext
                .selectCount()
                .from(BOOKING)
                .where(BOOKING.USER_ID.eq(userId))
                .fetchOne(0, Integer.class);

        // Count active bookings (booking_status = CONFIRMED or PENDING)
        Integer activeBookings = dslContext
                .selectCount()
                .from(BOOKING)
                .where(BOOKING.USER_ID.eq(userId)
                        .and(BOOKING.BOOKING_STATUS.in("CONFIRMED", "PENDING")))
                .fetchOne(0, Integer.class);

        // Sum total donations (using donation_amount column)
        BigDecimal totalDonations = dslContext
                .select(DONATION.DONATION_AMOUNT.sum())
                .from(DONATION)
                .where(DONATION.USER_ID.eq(userId))
                .fetchOne(0, BigDecimal.class);

        // Event registrations not yet implemented (table doesn't exist)
        Integer eventRegistrations = 0;

        return UserStatistics.builder()
                .totalBookings(totalBookings != null ? totalBookings : 0)
                .activeBookings(activeBookings != null ? activeBookings : 0)
                .totalDonations(totalDonations != null ? totalDonations : BigDecimal.ZERO)
                .eventRegistrations(eventRegistrations)
                .build();
    }
}

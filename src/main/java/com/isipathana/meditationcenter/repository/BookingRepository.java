package com.isipathana.meditationcenter.repository;

import com.isipathana.meditationcenter.records.booking.Booking;
import com.isipathana.meditationcenter.records.booking.BookingStatus;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.isipathana.meditationcenter.jooq.Tables.BOOKING;

@Repository
@RequiredArgsConstructor
public class BookingRepository {

    private final DSLContext dslContext;

    /**
     * Find booking by ID
     */
    public Booking findById(Long bookingId) {
        return dslContext
            .select(
                BOOKING.BOOKING_ID,
                BOOKING.MEDITATION_PROGRAM_ID,
                BOOKING.PRICING_ID,
                BOOKING.USER_ID,
                BOOKING.BOOKING_TYPE,
                BOOKING.BOOKING_DATE,
                BOOKING.STATUS,
                BOOKING.PARTICIPANT_COUNT,
                BOOKING.AMOUNT,
                BOOKING.SPECIAL_REQUIREMENTS,
                BOOKING.CANCELLATION_REASON,
                BOOKING.CREATED_AT,
                BOOKING.UPDATED_AT
            )
            .from(BOOKING)
            .where(BOOKING.BOOKING_ID.eq(bookingId))
            .fetchOne(record ->
                Booking.builder()
                    .bookingId(record.get(BOOKING.BOOKING_ID))
                    .meditationProgramId(record.get(BOOKING.MEDITATION_PROGRAM_ID))
                    .pricingId(record.get(BOOKING.PRICING_ID))
                    .userId(record.get(BOOKING.USER_ID))
                    .bookingType(record.get(BOOKING.BOOKING_TYPE))
                    .bookingDate(record.get(BOOKING.BOOKING_DATE))
                    .status(record.get(BOOKING.STATUS))
                    .participantCount(record.get(BOOKING.PARTICIPANT_COUNT))
                    .amount(record.get(BOOKING.AMOUNT))
                    .specialRequirements(record.get(BOOKING.SPECIAL_REQUIREMENTS))
                    .cancellationReason(record.get(BOOKING.CANCELLATION_REASON))
                    .createdAt(record.get(BOOKING.CREATED_AT))
                    .updatedAt(record.get(BOOKING.UPDATED_AT))
                    .build()
            );
    }

    /**
     * Find bookings by user ID
     */
    public List<Booking> findByUserId(Long userId) {
        return dslContext
            .select(
                BOOKING.BOOKING_ID,
                BOOKING.MEDITATION_PROGRAM_ID,
                BOOKING.PRICING_ID,
                BOOKING.USER_ID,
                BOOKING.BOOKING_TYPE,
                BOOKING.BOOKING_DATE,
                BOOKING.STATUS,
                BOOKING.PARTICIPANT_COUNT,
                BOOKING.AMOUNT,
                BOOKING.SPECIAL_REQUIREMENTS,
                BOOKING.CANCELLATION_REASON,
                BOOKING.CREATED_AT,
                BOOKING.UPDATED_AT
            )
            .from(BOOKING)
            .where(BOOKING.USER_ID.eq(userId))
            .orderBy(BOOKING.BOOKING_DATE.desc())
            .fetch(record ->
                Booking.builder()
                    .bookingId(record.get(BOOKING.BOOKING_ID))
                    .meditationProgramId(record.get(BOOKING.MEDITATION_PROGRAM_ID))
                    .pricingId(record.get(BOOKING.PRICING_ID))
                    .userId(record.get(BOOKING.USER_ID))
                    .bookingType(record.get(BOOKING.BOOKING_TYPE))
                    .bookingDate(record.get(BOOKING.BOOKING_DATE))
                    .status(record.get(BOOKING.STATUS))
                    .participantCount(record.get(BOOKING.PARTICIPANT_COUNT))
                    .amount(record.get(BOOKING.AMOUNT))
                    .specialRequirements(record.get(BOOKING.SPECIAL_REQUIREMENTS))
                    .cancellationReason(record.get(BOOKING.CANCELLATION_REASON))
                    .createdAt(record.get(BOOKING.CREATED_AT))
                    .updatedAt(record.get(BOOKING.UPDATED_AT))
                    .build()
            );
    }

    /**
     * Find bookings by status
     */
    public List<Booking> findByStatus(BookingStatus status) {
        return dslContext
            .select(
                BOOKING.BOOKING_ID,
                BOOKING.MEDITATION_PROGRAM_ID,
                BOOKING.PRICING_ID,
                BOOKING.USER_ID,
                BOOKING.BOOKING_TYPE,
                BOOKING.BOOKING_DATE,
                BOOKING.STATUS,
                BOOKING.PARTICIPANT_COUNT,
                BOOKING.AMOUNT,
                BOOKING.SPECIAL_REQUIREMENTS,
                BOOKING.CANCELLATION_REASON,
                BOOKING.CREATED_AT,
                BOOKING.UPDATED_AT
            )
            .from(BOOKING)
            .where(BOOKING.STATUS.eq(status))
            .orderBy(BOOKING.BOOKING_DATE.desc())
            .fetch(record ->
                Booking.builder()
                    .bookingId(record.get(BOOKING.BOOKING_ID))
                    .meditationProgramId(record.get(BOOKING.MEDITATION_PROGRAM_ID))
                    .pricingId(record.get(BOOKING.PRICING_ID))
                    .userId(record.get(BOOKING.USER_ID))
                    .bookingType(record.get(BOOKING.BOOKING_TYPE))
                    .bookingDate(record.get(BOOKING.BOOKING_DATE))
                    .status(record.get(BOOKING.STATUS))
                    .participantCount(record.get(BOOKING.PARTICIPANT_COUNT))
                    .amount(record.get(BOOKING.AMOUNT))
                    .specialRequirements(record.get(BOOKING.SPECIAL_REQUIREMENTS))
                    .cancellationReason(record.get(BOOKING.CANCELLATION_REASON))
                    .createdAt(record.get(BOOKING.CREATED_AT))
                    .updatedAt(record.get(BOOKING.UPDATED_AT))
                    .build()
            );
    }

    /**
     * Find bookings by date range
     */
    public List<Booking> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return dslContext
            .select(
                BOOKING.BOOKING_ID,
                BOOKING.MEDITATION_PROGRAM_ID,
                BOOKING.PRICING_ID,
                BOOKING.USER_ID,
                BOOKING.BOOKING_TYPE,
                BOOKING.BOOKING_DATE,
                BOOKING.STATUS,
                BOOKING.PARTICIPANT_COUNT,
                BOOKING.AMOUNT,
                BOOKING.SPECIAL_REQUIREMENTS,
                BOOKING.CANCELLATION_REASON,
                BOOKING.CREATED_AT,
                BOOKING.UPDATED_AT
            )
            .from(BOOKING)
            .where(BOOKING.BOOKING_DATE.between(startDate, endDate))
            .orderBy(BOOKING.BOOKING_DATE.asc())
            .fetch(record ->
                Booking.builder()
                    .bookingId(record.get(BOOKING.BOOKING_ID))
                    .meditationProgramId(record.get(BOOKING.MEDITATION_PROGRAM_ID))
                    .pricingId(record.get(BOOKING.PRICING_ID))
                    .userId(record.get(BOOKING.USER_ID))
                    .bookingType(record.get(BOOKING.BOOKING_TYPE))
                    .bookingDate(record.get(BOOKING.BOOKING_DATE))
                    .status(record.get(BOOKING.STATUS))
                    .participantCount(record.get(BOOKING.PARTICIPANT_COUNT))
                    .amount(record.get(BOOKING.AMOUNT))
                    .specialRequirements(record.get(BOOKING.SPECIAL_REQUIREMENTS))
                    .cancellationReason(record.get(BOOKING.CANCELLATION_REASON))
                    .createdAt(record.get(BOOKING.CREATED_AT))
                    .updatedAt(record.get(BOOKING.UPDATED_AT))
                    .build()
            );
    }
}

package com.isipathana.meditationcenter.models.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Generic response wrapper for paginated search results using offset-based pagination.
 * Contains the list of results along with pagination metadata.
 *
 * @param <T> Type of response data (must implement ApiResponse)
 * @author Sathira Basnayake
 */
@RequiredArgsConstructor
@Getter
public class OffsetSearchResponse<T extends ApiResponse> implements ApiResponse {
    private final List<T> data;
    private final long currentOffset;
    private final long maxOffset;

    /**
     * Factory for creating OffsetSearchResponse instances.
     * This is a Spring-managed component that can be injected into presenters.
     */
    @Component
    public static class Factory {
        /**
         * Create a response object with a list of data.
         *
         * @param data          List of response data
         * @param currentOffset Current offset (starting position)
         * @param maxOffset     Maximum offset (total count)
         * @param <R>           Response type
         * @return OffsetSearchResponse instance
         */
        public <R extends ApiResponse> OffsetSearchResponse<R> create(
                List<R> data, long currentOffset, long maxOffset) {
            return new OffsetSearchResponse<>(data, currentOffset, maxOffset);
        }

        /**
         * Create a response object with a single data item.
         *
         * @param data          Single response data item
         * @param currentOffset Current offset
         * @param maxOffset     Maximum offset
         * @param <R>           Response type
         * @return OffsetSearchResponse instance containing a single-item list
         */
        public <R extends ApiResponse> OffsetSearchResponse<R> create(
                R data, long currentOffset, long maxOffset) {
            return new OffsetSearchResponse<>(List.of(data), currentOffset, maxOffset);
        }
    }
}

package com.isipathana.meditationcenter.rest.admin.override.get;

import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Presenter for GetOverrides response.
 *
 * @author Sathira Basnayake
 */
@Component
public class GetOverridesPresenter {

    public OffsetSearchResponse<GetOverridesResponse> build(
            List<GetOverridesResponse> overrides,
            int currentOffset,
            int totalCount
    ) {
        int maxOffset = Math.max(0, totalCount - 1);

        return new OffsetSearchResponse<>(
                overrides,
                currentOffset,
                maxOffset
        );
    }
}

package com.isipathana.meditationcenter.rest.admin.override.get;

import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.architecture.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * UseCase for retrieving schedule overrides with pagination and filtering.
 *
 * @author Sathira Basnayake
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GetOverridesUseCase implements UseCase<GetOverridesRequest, OffsetSearchResponse<GetOverridesResponse>> {

    private final GetOverridesDataAccess repository;
    private final GetOverridesPresenter presenter;

    @Transactional(readOnly = true)
    @Override
    public OffsetSearchResponse<GetOverridesResponse> handle(GetOverridesRequest request) {
        log.info("Fetching overrides: page={}, limit={}, fromDate={}, toDate={}",
                request.page(), request.limit(), request.fromDate(), request.toDate());

        int offset = (request.page() - 1) * request.limit();

        List<GetOverridesResponse> overrides = repository.getOverrides(
                offset,
                request.limit(),
                request.fromDate(),
                request.toDate()
        );

        int totalCount = repository.countOverrides(request.fromDate(), request.toDate());

        return presenter.build(overrides, offset, totalCount);
    }
}

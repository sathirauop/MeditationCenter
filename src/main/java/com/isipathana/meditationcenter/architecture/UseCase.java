package com.isipathana.meditationcenter.architecture;

/**
 * Base interface for all use cases.
 * A use case represents a single business operation that can be invoked from a controller.
 *
 * @param <Request> The request object type
 * @param <Response> The response object type
 */
public interface UseCase<Request, Response> {

    /**
     * Handles the business logic for this use case.
     *
     * @param request The request object containing input data
     * @return The response object containing the result
     */
    Response handle(Request request);
}

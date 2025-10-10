package com.isipathana.meditationcenter.architecture;

/**
 * Base interface for use cases that require authentication.
 *
 * @param <Principal> The authentication principal type (e.g., User, UserDetails)
 * @param <Request> The request object type
 * @param <Response> The response object type
 */
public interface AuthenticatedUseCase<Principal, Request, Response> {

    /**
     * Handles the business logic for this authenticated use case.
     *
     * @param principal The authenticated user/principal
     * @param request The request object containing input data
     * @return The response object containing the result
     */
    Response handle(Principal principal, Request request);
}

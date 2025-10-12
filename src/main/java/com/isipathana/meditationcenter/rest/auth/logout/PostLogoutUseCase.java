package com.isipathana.meditationcenter.rest.auth.logout;

import org.springframework.stereotype.Service;

/**
 * UseCase for user logout.
 * <p>
 * Note: Since JWT is stateless, logout is primarily a client-side operation.
 * Client should delete stored tokens. In the future, this can implement
 * token blacklist for server-side revocation.
 */
@Service
public class PostLogoutUseCase {

    /**
     * Execute user logout.
     * Currently a no-op as JWT logout is client-side.
     * Future enhancement: Add token to blacklist for server-side revocation.
     */
    public void execute() {
        // TODO: Implement token blacklist in future
        // For now, client handles logout by deleting tokens
    }
}

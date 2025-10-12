package com.isipathana.meditationcenter.rest.auth;

import com.isipathana.meditationcenter.rest.auth.login.PostLoginRequest;
import com.isipathana.meditationcenter.rest.auth.login.PostLoginResponse;
import com.isipathana.meditationcenter.rest.auth.login.PostLoginUseCase;
import com.isipathana.meditationcenter.rest.auth.logout.PostLogoutUseCase;
import com.isipathana.meditationcenter.rest.auth.refresh.PostRefreshRequest;
import com.isipathana.meditationcenter.rest.auth.refresh.PostRefreshResponse;
import com.isipathana.meditationcenter.rest.auth.refresh.PostRefreshUseCase;
import com.isipathana.meditationcenter.rest.auth.register.PostRegisterRequest;
import com.isipathana.meditationcenter.rest.auth.register.PostRegisterResponse;
import com.isipathana.meditationcenter.rest.auth.register.PostRegisterUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Thin REST controller for authentication endpoints.
 * <p>
 * All endpoints are public (no authentication required).
 * Configured in SecurityConfig.java.
 * <p>
 * This controller only handles HTTP concerns (routing, validation, status codes).
 * All business logic is delegated to UseCases.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final PostRegisterUseCase postRegisterUseCase;
    private final PostLoginUseCase postLoginUseCase;
    private final PostRefreshUseCase postRefreshUseCase;
    private final PostLogoutUseCase postLogoutUseCase;

    /**
     * Register a new user.
     * <p>
     * POST /api/auth/register
     *
     * @param request Registration request with email, password, name, mobileNumber
     * @return 201 Created with access token and refresh token
     */
    @PostMapping("/register")
    public ResponseEntity<PostRegisterResponse> register(@Valid @RequestBody PostRegisterRequest request) {
        PostRegisterResponse response = postRegisterUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Login user and generate tokens.
     * <p>
     * POST /api/auth/login
     *
     * @param request Login request with email and password
     * @return 200 OK with access token and refresh token
     */
    @PostMapping("/login")
    public ResponseEntity<PostLoginResponse> login(@Valid @RequestBody PostLoginRequest request) {
        PostLoginResponse response = postLoginUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Refresh access token using refresh token.
     * <p>
     * POST /api/auth/refresh
     *
     * @param request Refresh token request
     * @return 200 OK with new access token (no refresh token)
     */
    @PostMapping("/refresh")
    public ResponseEntity<PostRefreshResponse> refresh(@Valid @RequestBody PostRefreshRequest request) {
        PostRefreshResponse response = postRefreshUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Logout user (client-side operation).
     * <p>
     * POST /api/auth/logout
     * <p>
     * Note: Since JWT is stateless, logout is primarily a client-side operation.
     * Client should delete stored tokens. In the future, this endpoint can
     * implement token blacklist for additional security.
     *
     * @return 200 OK
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        postLogoutUseCase.execute();
        return ResponseEntity.ok().build();
    }
}

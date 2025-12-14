package com.isipathana.meditationcenter.rest.admin;

import com.isipathana.meditationcenter.constants.EndPoints;
import com.isipathana.meditationcenter.records.user.UserRole;
import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.rest.admin.user.get.GetUsersRequest;
import com.isipathana.meditationcenter.rest.admin.user.get.GetUsersResponse;
import com.isipathana.meditationcenter.rest.admin.user.get.GetUsersUseCase;
import com.isipathana.meditationcenter.rest.admin.user.getById.GetUserByIdResponse;
import com.isipathana.meditationcenter.rest.admin.user.getById.GetUserByIdUseCase;
import com.isipathana.meditationcenter.rest.admin.user.post.PostUserRequest;
import com.isipathana.meditationcenter.rest.admin.user.post.PostUserResponse;
import com.isipathana.meditationcenter.rest.admin.user.post.PostUserUseCase;
import com.isipathana.meditationcenter.rest.admin.user.patch.PatchUserRequest;
import com.isipathana.meditationcenter.rest.admin.user.patch.PatchUserResponse;
import com.isipathana.meditationcenter.rest.admin.user.patch.PatchUserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for admin user management endpoints.
 * All endpoints require ADMIN role with appropriate permissions.
 *
 * @author Sathira Basnayake
 */
@RestController
@RequestMapping(EndPoints.Admin.User.BASE)
@RequiredArgsConstructor
public class AdminUserController {

    private final GetUsersUseCase getUsersUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final PostUserUseCase postUserUseCase;
    private final PatchUserUseCase patchUserUseCase;

    /**
     * Get paginated list of users with optional filtering.
     * <p>
     * GET /api/admin/users
     * <p>
     * Requires: ADMIN role with VIEW_USERS permission
     * <p>
     * Query Parameters:
     * - limit (optional, default: 20, max: 100) - Results per page
     * - offset (optional, default: 0) - Pagination offset
     * - role (optional: USER, ADMIN) - Filter by role
     * - isActive (optional: true/false) - Filter by active status
     * - search (optional) - Search by name or email
     *
     * @param limit Maximum results per page
     * @param offset Pagination offset
     * @param role Filter by role
     * @param isActive Filter by active status
     * @param search Search term for name/email
     * @return 200 OK with paginated user list
     */
    @GetMapping(EndPoints.Admin.User.GET_ALL)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('VIEW_USERS')")
    public ResponseEntity<OffsetSearchResponse<GetUsersResponse>> getUsers(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String search
    ) {
        GetUsersRequest request = new GetUsersRequest(limit, offset, role, isActive, search);
        OffsetSearchResponse<GetUsersResponse> response = getUsersUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get detailed user information by ID with statistics.
     * <p>
     * GET /api/admin/users/{userId}
     * <p>
     * Requires: ADMIN role with VIEW_USERS permission
     * <p>
     * Includes statistics:
     * - Total bookings count
     * - Active bookings count
     * - Total donations amount
     * - Event registrations count
     *
     * @param userId User ID
     * @return 200 OK with user details and statistics
     */
    @GetMapping(EndPoints.Admin.User.GET_BY_ID)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('VIEW_USERS')")
    public ResponseEntity<GetUserByIdResponse> getUserById(@PathVariable Long userId) {
        GetUserByIdResponse response = getUserByIdUseCase.execute(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Create a new user.
     * <p>
     * POST /api/admin/users
     * <p>
     * Requires: ADMIN role with CREATE_USER permission
     * <p>
     * Request body must include:
     * - email (valid email format)
     * - password (minimum 8 characters)
     * - name
     * - role (USER or ADMIN)
     * - mobileNumber (optional)
     * - isActive (optional, defaults to true)
     * - emailVerified (optional, defaults to false)
     *
     * @param request User creation request
     * @return 201 Created with created user details
     */
    @PostMapping(EndPoints.Admin.User.CREATE)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('CREATE_USER')")
    public ResponseEntity<PostUserResponse> createUser(@Valid @RequestBody PostUserRequest request) {
        PostUserResponse response = postUserUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update user information.
     * <p>
     * PATCH /api/admin/users/{userId}
     * <p>
     * Requires: ADMIN role with UPDATE_USER permission
     * <p>
     * All fields are optional - only provided fields will be updated:
     * - email (must be valid if provided)
     * - password (minimum 8 characters if provided)
     * - name
     * - mobileNumber
     *
     * @param userId  User ID
     * @param request Update request with fields to change
     * @return 200 OK with updated user details
     */
    @PatchMapping(EndPoints.Admin.User.UPDATE)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('UPDATE_USER')")
    public ResponseEntity<PatchUserResponse> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody PatchUserRequest request
    ) {
        PatchUserResponse response = patchUserUseCase.execute(userId, request);
        return ResponseEntity.ok(response);
    }
}

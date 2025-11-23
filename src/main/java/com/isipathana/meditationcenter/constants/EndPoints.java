package com.isipathana.meditationcenter.constants;

/**
 * Centralized endpoint constants for the Meditation Center API.
 * <p>
 * All REST endpoint paths should be defined here to ensure consistency
 * across controllers and security configuration.
 * <p>
 * Usage:
 * - Use BASE paths for @RequestMapping on controllers
 * - Use full paths for method-level mappings and security configuration
 *
 * @author Sathira Basnayake
 */
public final class EndPoints {

    private EndPoints() {
        // Private constructor to prevent instantiation
    }

    // ============================================================
    // BASE PATHS
    // ============================================================

    /**
     * API base path prefix
     */
    public static final String API = "/api";

    /**
     * Root endpoint
     */
    public static final String ROOT = "/";

    /**
     * Error endpoint
     */
    public static final String ERROR = "/error";

    // ============================================================
    // AUTH ENDPOINTS
    // ============================================================

    public static final class Auth {
        private Auth() {}

        /**
         * Base path for authentication endpoints
         */
        public static final String BASE = API + "/auth";

        /**
         * POST /api/auth/register - User registration
         */
        public static final String REGISTER = "/register";

        /**
         * POST /api/auth/login - User login
         */
        public static final String LOGIN = "/login";

        /**
         * POST /api/auth/refresh - Refresh access token
         */
        public static final String REFRESH = "/refresh";

        /**
         * POST /api/auth/logout - User logout
         */
        public static final String LOGOUT = "/logout";

        // Full paths for security configuration
        public static final String FULL_PATH = BASE + "/**";
    }

    // ============================================================
    // EVENT ENDPOINTS (Public)
    // ============================================================

    public static final class Event {
        private Event() {}

        /**
         * Base path for public event endpoints
         */
        public static final String BASE = API + "/event";

        /**
         * GET /api/event - Get all active events (public)
         */
        public static final String GET_ALL = "";

        /**
         * GET /api/event/{id} - Get event by ID (public)
         */
        public static final String GET_BY_ID = "/{id}";

        // Full paths for security configuration
        public static final String FULL_PATH = BASE;
        public static final String FULL_PATH_BY_ID = BASE + "/{id}";
    }

    // ============================================================
    // PROGRAM ENDPOINTS (Public)
    // ============================================================

    public static final class Program {
        private Program() {}

        /**
         * Base path for public program endpoints
         */
        public static final String BASE = API + "/program";

        /**
         * GET /api/program - Get all programs (public)
         */
        public static final String GET_ALL = "";

        /**
         * GET /api/program/{id} - Get program by ID (public)
         */
        public static final String GET_BY_ID = "/{id}";

        // Full paths for security configuration
        public static final String FULL_PATH = BASE;
        public static final String FULL_PATH_BY_ID = BASE + "/{id}";
    }

    // ============================================================
    // ADMIN ENDPOINTS
    // ============================================================

    public static final class Admin {
        private Admin() {}

        /**
         * Base path for admin endpoints
         */
        public static final String BASE = API + "/admin";

        /**
         * Admin Event Management
         */
        public static final class Event {
            private Event() {}

            /**
             * Base path for admin event endpoints
             */
            public static final String BASE = Admin.BASE + "/event";

            /**
             * GET /api/admin/event - Get admin events list
             */
            public static final String GET_ALL = "";

            /**
             * POST /api/admin/event - Create event with multipart/form-data
             */
            public static final String CREATE = "";

            /**
             * POST /api/admin/event/json - Create event with JSON
             */
            public static final String CREATE_JSON = "/json";

            /**
             * DELETE /api/admin/event/{eventId} - Delete event by ID
             */
            public static final String DELETE = "/{eventId}";

            // Full paths for security configuration
            public static final String FULL_PATH = BASE + "/**";
        }
    }

    // ============================================================
    // UTILITY ENDPOINTS (Development Only)
    // ============================================================

    public static final class Util {
        private Util() {}

        /**
         * Base path for utility endpoints
         */
        public static final String BASE = API + "/util";

        /**
         * GET /api/util/hash - Generate password hash
         */
        public static final String HASH = "/hash";

        // Full paths for security configuration
        public static final String FULL_PATH = BASE + "/**";
    }
}

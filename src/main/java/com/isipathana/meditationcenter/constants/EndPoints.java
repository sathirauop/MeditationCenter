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

    public static final class Public {
        private Public() {}

        /**
         * Public Program Management
         */
        public static final class Program {
            private Program() {}

            /**
             * Base path for public program endpoints
             */
            public static final String BASE = API + "/programs";

            /**
             * GET /api/programs/active - Get the currently active program (public)
             */
            public static final String GET_ACTIVE = "/active";

            /**
             * GET /api/programs/{id} - Get active program by ID (public)
             */
            public static final String GET_BY_ID = "/{id}";

            // Full paths for security configuration
            public static final String FULL_PATH = BASE + "/**";
        }
    }

    // ============================================================
    // SCHEDULE ENDPOINTS (Public)
    // ============================================================

    public static final class Schedule {
        private Schedule() {}

        /**
         * Base path for public schedule endpoints
         */
        public static final String BASE = API + "/schedule";

        /**
         * GET /api/schedule/today - Get today's schedule (public)
         */
        public static final String GET_TODAY = "/today";

        /**
         * GET /api/schedule/{date} - Get schedule by date (public)
         */
        public static final String GET_BY_DATE = "/{date}";

        // Full paths for security configuration
        public static final String FULL_PATH = BASE + "/**";
    }

    // ============================================================
    // BOOK ENDPOINTS (Public)
    // ============================================================

    public static final class Book {
        private Book() {}

        /**
         * Base path for public book endpoints
         */
        public static final String BASE = API + "/books";

        /**
         * GET /api/books - Get all active books (public)
         */
        public static final String GET_ALL = "";

        // Full paths for security configuration
        public static final String FULL_PATH = BASE + "/**";
    }

    // ============================================================
    // BLOG ENDPOINTS (Public)
    // ============================================================

    public static final class Blog {
        private Blog() {}

        /**
         * Base path for public blog endpoints
         */
        public static final String BASE = API + "/blog";

        /**
         * GET /api/blog - Get all published blog posts (public, paginated, filterable)
         */
        public static final String GET_ALL = "";

        /**
         * GET /api/blog/{slug} - Get single published blog post by slug (public)
         */
        public static final String GET_BY_SLUG = "/{slug}";

        /**
         * GET /api/blog/tags - Get all tags (public)
         */
        public static final String GET_TAGS = "/tags";

        // Full paths for security configuration
        public static final String FULL_PATH = BASE;
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
             * PATCH /api/admin/event/{eventId} - Update event by ID
             */
            public static final String UPDATE = "/{eventId}";

            /**
             * DELETE /api/admin/event/{eventId} - Delete event by ID
             */
            public static final String DELETE = "/{eventId}";

            // Full paths for security configuration
            public static final String FULL_PATH = BASE + "/**";
        }

        /**
         * Admin Program Management
         */
        public static final class Program {
            private Program() {}

            /**
             * Base path for admin program endpoints
             */
            public static final String BASE = Admin.BASE + "/programs";

            /**
             * POST /api/admin/program - Create program with multipart/form-data
             */
            public static final String CREATE = "";

            /**
             * POST /api/admin/program/json - Create program with JSON
             */
            public static final String CREATE_JSON = "/json";

            /**
             * GET /api/admin/programs/active - Get the currently active program
             */
            public static final String GET_ACTIVE = "/active";

            /**
             * GET /api/admin/programs/{programId} - Get program by ID
             */
            public static final String GET_BY_ID = "/{programId}";

            /**
             * PATCH /api/admin/programs/{programId} - Update program by ID
             */
            public static final String UPDATE = "/{programId}";

            // Full paths for security configuration
            public static final String FULL_PATH = BASE + "/**";
            public static final String FULL_PATH_PROGRAMS = Admin.BASE + "/programs/**";
        }

        /**
         * Admin Activity Management
         */
        public static final class Activity {
            private Activity() {}

            /**
             * Base path for admin activity endpoints
             */
            public static final String BASE = Admin.BASE + "/activities";

            /**
             * POST /api/admin/activities - Create activity
             */
            public static final String CREATE = "";

            /**
             * GET /api/admin/activities - Get all activities
             */
            public static final String GET_ALL = "";

            /**
             * GET /api/admin/activities/{id} - Get activity by ID
             */
            public static final String GET_BY_ID = "/{id}";

            /**
             * PATCH /api/admin/activities/{id} - Update activity
             */
            public static final String UPDATE = "/{id}";

            /**
             * DELETE /api/admin/activities/{id} - Delete activity
             */
            public static final String DELETE = "/{id}";

            // Full paths for security configuration
            public static final String FULL_PATH = BASE + "/**";
        }

        /**
         * Admin Template Management
         */
        public static final class Template {
            private Template() {}

            /**
             * Base path for admin template endpoints
             */
            public static final String BASE = Admin.BASE + "/templates";

            /**
             * POST /api/admin/templates - Create template
             */
            public static final String CREATE = "";

            /**
             * GET /api/admin/templates - Get all templates
             */
            public static final String GET_ALL = "";

            /**
             * GET /api/admin/templates/active - Get active template
             */
            public static final String GET_ACTIVE = "/active";

            /**
             * GET /api/admin/templates/{id} - Get template by ID
             */
            public static final String GET_BY_ID = "/{id}";

            /**
             * PUT /api/admin/templates/{id} - Update template
             */
            public static final String UPDATE = "/{id}";

            /**
             * PATCH /api/admin/templates/{id}/activate - Activate template
             */
            public static final String ACTIVATE = "/{id}/activate";

            /**
             * DELETE /api/admin/templates/{id} - Delete template
             */
            public static final String DELETE = "/{id}";

            // Full paths for security configuration
            public static final String FULL_PATH = BASE + "/**";

            /**
             * Template Activity Management
             */
            public static final class Activities {
                private Activities() {}

                /**
                 * POST /api/admin/templates/{id}/activities - Add activity to template
                 */
                public static final String ADD = "/{id}/activities";

                /**
                 * PUT /api/admin/templates/{templateId}/activities/{activityId} - Update template activity
                 */
                public static final String UPDATE = "/{templateId}/activities/{activityId}";

                /**
                 * DELETE /api/admin/templates/{templateId}/activities/{activityId} - Remove activity
                 */
                public static final String DELETE = "/{templateId}/activities/{activityId}";

                /**
                 * PUT /api/admin/templates/{id}/activities/bulk - Bulk update activities
                 */
                public static final String BULK_UPDATE = "/{id}/activities/bulk";
            }
        }

        /**
         * Admin Override Management
         */
        public static final class Override {
            private Override() {}

            /**
             * Base path for admin override endpoints
             */
            public static final String BASE = Admin.BASE + "/overrides";

            /**
             * POST /api/admin/overrides - Create override
             */
            public static final String CREATE = "";

            /**
             * GET /api/admin/overrides - Get all overrides
             */
            public static final String GET_ALL = "";

            /**
             * GET /api/admin/overrides/{date} - Get override by date
             */
            public static final String GET_BY_DATE = "/{date}";

            /**
             * PUT /api/admin/overrides/{id} - Update override
             */
            public static final String UPDATE = "/{id}";

            /**
             * DELETE /api/admin/overrides/{id} - Delete override by ID
             */
            public static final String DELETE = "/{id}";

            /**
             * DELETE /api/admin/overrides/by-date/{date} - Delete override by date
             */
            public static final String DELETE_BY_DATE = "/by-date/{date}";

            // Full paths for security configuration
            public static final String FULL_PATH = BASE + "/**";

            /**
             * Override Activity Management
             */
            public static final class Activities {
                private Activities() {}

                /**
                 * POST /api/admin/overrides/{id}/activities - Add activity to override
                 */
                public static final String ADD = "/{id}/activities";

                /**
                 * DELETE /api/admin/overrides/{overrideId}/activities/{activityId} - Remove activity
                 */
                public static final String DELETE = "/{overrideId}/activities/{activityId}";
            }
        }

        /**
         * Admin Book Management
         */
        public static final class Book {
            private Book() {}

            /**
             * Base path for admin book endpoints
             */
            public static final String BASE = Admin.BASE + "/book";

            /**
             * GET /api/admin/book - Get all books (admin)
             */
            public static final String GET_ALL = "";

            /**
             * POST /api/admin/book - Create book with multipart/form-data
             */
            public static final String CREATE = "";

            /**
             * POST /api/admin/book/json - Create book with JSON (testing)
             */
            public static final String CREATE_JSON = "/json";

            /**
             * PATCH /api/admin/book/{bookId} - Update book by ID
             */
            public static final String UPDATE = "/{bookId}";

            // Full paths for security configuration
            public static final String FULL_PATH = BASE + "/**";
        }

        /**
         * Admin Blog Management (ADMIN only)
         */
        public static final class Blog {
            private Blog() {}

            /**
             * Base path for admin blog endpoints
             */
            public static final String BASE = Admin.BASE + "/blog";

            /**
             * GET /api/admin/blog - Get all blog posts including drafts (admin)
             */
            public static final String GET_ALL = "";

            /**
             * GET /api/admin/blog/{postId} - Get blog post by ID (for editing)
             */
            public static final String GET_BY_ID = "/{postId}";

            /**
             * POST /api/admin/blog - Create blog post with multipart/form-data
             */
            public static final String CREATE = "";

            /**
             * PATCH /api/admin/blog/{postId} - Update blog post
             */
            public static final String UPDATE = "/{postId}";

            /**
             * PATCH /api/admin/blog/drafts/{postId} - Auto-save draft (no validation)
             */
            public static final String AUTO_SAVE_DRAFT = "/drafts/{postId}";

            /**
             * DELETE /api/admin/blog/{postId} - Soft delete blog post
             */
            public static final String DELETE = "/{postId}";

            /**
             * POST /api/admin/blog/{postId}/publish - Publish draft post
             */
            public static final String PUBLISH = "/{postId}/publish";

            /**
             * POST /api/admin/blog/{postId}/unpublish - Unpublish post (revert to draft)
             */
            public static final String UNPUBLISH = "/{postId}/unpublish";

            // Full paths for security configuration
            public static final String FULL_PATH = BASE + "/**";

            /**
             * Admin Blog Tag Management
             */
            public static final class Tags {
                private Tags() {}

                /**
                 * GET /api/admin/blog/tags - Get all tags (admin view)
                 */
                public static final String GET_ALL = "/tags";

                /**
                 * POST /api/admin/blog/tags - Create new tag
                 */
                public static final String CREATE = "/tags";

                /**
                 * PATCH /api/admin/blog/tags/{tagId} - Update tag
                 */
                public static final String UPDATE = "/tags/{tagId}";

                /**
                 * DELETE /api/admin/blog/tags/{tagId} - Delete tag
                 */
                public static final String DELETE = "/tags/{tagId}";
            }
        }

        /**
         * Admin User Management
         */
        public static final class User {
            private User() {}

            /**
             * Base path for admin user endpoints
             */
            public static final String BASE = Admin.BASE + "/users";

            /**
             * GET /api/admin/users - Get all users (paginated)
             */
            public static final String GET_ALL = "";

            /**
             * GET /api/admin/users/{userId} - Get user by ID
             */
            public static final String GET_BY_ID = "/{userId}";

            /**
             * POST /api/admin/users - Create new user
             */
            public static final String CREATE = "";

            /**
             * PATCH /api/admin/users/{userId} - Update user information
             */
            public static final String UPDATE = "/{userId}";

            /**
             * PATCH /api/admin/users/{userId}/role - Update user role
             */
            public static final String UPDATE_ROLE = "/{userId}/role";

            /**
             * PATCH /api/admin/users/{userId}/activate - Activate user
             */
            public static final String ACTIVATE = "/{userId}/activate";

            /**
             * PATCH /api/admin/users/{userId}/deactivate - Deactivate user
             */
            public static final String DEACTIVATE = "/{userId}/deactivate";

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

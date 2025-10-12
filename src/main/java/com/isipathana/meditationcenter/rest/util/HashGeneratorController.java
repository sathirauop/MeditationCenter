package com.isipathana.meditationcenter.rest.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Utility controller to generate BCrypt password hashes.
 * USE ONLY IN DEVELOPMENT - DELETE IN PRODUCTION!
 *
 * @author Sathira Basnayake
 */
@RestController
@RequestMapping("/api/util")
@RequiredArgsConstructor
public class HashGeneratorController {

    private final PasswordEncoder passwordEncoder;

    /**
     * Generate BCrypt hash for a given password.
     * <p>
     * GET /api/util/hash?password=admin123
     * <p>
     * ⚠️ WARNING: This endpoint should be REMOVED or SECURED before production!
     *
     * @param password Plain text password to hash
     * @return BCrypt hash
     */
    @GetMapping("/hash")
    public String generateHash(@RequestParam String password) {
        return passwordEncoder.encode(password);
    }
}

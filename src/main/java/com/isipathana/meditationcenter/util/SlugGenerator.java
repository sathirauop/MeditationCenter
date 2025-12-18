package com.isipathana.meditationcenter.util;

import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Utility for generating URL-friendly slugs from text.
 * Slugs are lowercase, contain only alphanumeric characters and hyphens,
 * and are suitable for use in URLs.
 *
 * @author Sathira Basnayake
 */
@Component
public class SlugGenerator {

    /**
     * Generates a URL-friendly slug from the given text.
     * - Converts to lowercase
     * - Removes all special characters except spaces and hyphens
     * - Replaces spaces with hyphens
     * - Removes duplicate hyphens
     * - Trims leading/trailing hyphens
     *
     * @param text The text to convert to a slug
     * @return A URL-friendly slug
     */
    public String generate(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        return text.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")  // Remove special chars (keep spaces and hyphens)
                .trim()
                .replaceAll("\\s+", "-")          // Replace spaces with hyphens
                .replaceAll("-+", "-")            // Remove duplicate hyphens
                .replaceAll("^-|-$", "");         // Remove leading/trailing hyphens
    }

    /**
     * Ensures the slug is unique by appending a counter if necessary.
     * If the base slug already exists, appends "-2", "-3", etc. until a unique slug is found.
     *
     * @param baseSlug      The base slug to make unique
     * @param existsChecker Function that returns true if the slug already exists in the database
     * @return A unique slug
     */
    public String ensureUnique(String baseSlug, Function<String, Boolean> existsChecker) {
        String slug = baseSlug;
        int counter = 2;

        // Keep incrementing counter until we find a unique slug
        while (existsChecker.apply(slug)) {
            slug = baseSlug + "-" + counter++;
        }

        return slug;
    }

    /**
     * Validates that a slug follows the correct format.
     * Valid slugs contain only lowercase letters, numbers, and hyphens.
     * They cannot start or end with a hyphen.
     *
     * @param slug The slug to validate
     * @return true if the slug is valid, false otherwise
     */
    public boolean isValid(String slug) {
        if (slug == null || slug.isBlank()) {
            return false;
        }

        // Must match pattern: lowercase letters, numbers, and hyphens only
        // Cannot start or end with hyphen
        return slug.matches("^[a-z0-9]+(?:-[a-z0-9]+)*$");
    }
}

package com.isipathana.meditationcenter.rest.admin.gallery.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a gallery group.
 *
 * @author Sathira Basnayake
 */
public record PostGalleryGroupRequest(
        @NotBlank(message = "Group name is required") @Size(max = 255, message = "Group name must be at most 255 characters") String name,

        @Size(max = 255, message = "Sinhala group name must be at most 255 characters") String nameSi,

        Integer sortOrder) {
}

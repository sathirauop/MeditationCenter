package com.isipathana.meditationcenter.rest.admin.gallery.patch;

import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating a gallery group.
 *
 * @author Sathira Basnayake
 */
public record PatchGalleryGroupRequest(
        @Size(max = 255, message = "Group name must be at most 255 characters") String name,

        @Size(max = 255, message = "Sinhala group name must be at most 255 characters") String nameSi,

        Integer sortOrder,

        Boolean active) {
}

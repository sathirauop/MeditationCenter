package com.isipathana.meditationcenter.rest.admin.gallery.post;

import com.isipathana.meditationcenter.records.gallery.GalleryGroup;

/**
 * Data access interface for creating gallery groups.
 *
 * @author Sathira Basnayake
 */
public interface PostGalleryGroupDataAccess {

    /**
     * Creates a new gallery group.
     *
     * @param group The gallery group to create
     * @return The created gallery group with generated ID
     */
    GalleryGroup createGroup(GalleryGroup group);
}

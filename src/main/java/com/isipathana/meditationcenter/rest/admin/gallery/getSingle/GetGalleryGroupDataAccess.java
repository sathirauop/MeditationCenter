package com.isipathana.meditationcenter.rest.admin.gallery.getSingle;

import com.isipathana.meditationcenter.records.gallery.GalleryGroup;
import com.isipathana.meditationcenter.records.gallery.GalleryPhoto;

import java.util.List;

/**
 * Data access interface for fetching a single gallery group with photos.
 *
 * @author Sathira Basnayake
 */
public interface GetGalleryGroupDataAccess {

    /**
     * Fetches a gallery group by ID.
     *
     * @param groupId The group ID
     * @return The gallery group, or null if not found
     */
    GalleryGroup getGroupById(Long groupId);

    /**
     * Fetches all photos for a gallery group.
     *
     * @param groupId The group ID
     * @return List of photos sorted by sort_order
     */
    List<GalleryPhoto> getPhotosByGroupId(Long groupId);
}

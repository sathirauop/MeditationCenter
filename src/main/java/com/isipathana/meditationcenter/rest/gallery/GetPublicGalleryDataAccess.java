package com.isipathana.meditationcenter.rest.gallery;

import com.isipathana.meditationcenter.records.gallery.GalleryGroup;
import com.isipathana.meditationcenter.records.gallery.GalleryPhoto;

import java.util.List;

/**
 * Data access interface for public gallery endpoints.
 *
 * @author Sathira Basnayake
 */
public interface GetPublicGalleryDataAccess {

    /**
     * Fetches all active gallery groups ordered by sort_order.
     *
     * @return List of active gallery groups
     */
    List<GalleryGroup> getActiveGroups();

    /**
     * Fetches all photos for a list of group IDs.
     *
     * @param groupIds The group IDs
     * @return List of photos sorted by group and sort_order
     */
    List<GalleryPhoto> getPhotosByGroupIds(List<Long> groupIds);
}

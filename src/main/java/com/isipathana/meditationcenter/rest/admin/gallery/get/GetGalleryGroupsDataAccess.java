package com.isipathana.meditationcenter.rest.admin.gallery.get;

import java.util.List;

/**
 * Data access interface for fetching gallery groups.
 *
 * @author Sathira Basnayake
 */
public interface GetGalleryGroupsDataAccess {

    /**
     * Fetches all gallery groups with their photo counts.
     *
     * @return List of gallery groups with counts
     */
    List<GetGalleryGroupsResponse> getAllGroups();
}

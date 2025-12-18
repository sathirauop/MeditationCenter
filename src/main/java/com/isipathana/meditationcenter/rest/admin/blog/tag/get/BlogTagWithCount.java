package com.isipathana.meditationcenter.rest.admin.blog.tag.get;

import com.isipathana.meditationcenter.records.blog.BlogTag;

/**
 * Internal DTO for blog tag with post count.
 * Used to transfer tag data with aggregated post count from repository to presenter.
 *
 * @author Sathira Basnayake
 */
public record BlogTagWithCount(
        BlogTag tag,
        Long postCount
) {
}

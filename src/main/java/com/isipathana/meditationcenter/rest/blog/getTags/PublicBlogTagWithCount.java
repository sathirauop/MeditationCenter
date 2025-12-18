package com.isipathana.meditationcenter.rest.blog.getTags;

import com.isipathana.meditationcenter.records.blog.BlogTag;

/**
 * Internal DTO for blog tags with published post count.
 * Used by repository to return tag + count data.
 *
 * @param tag       The blog tag
 * @param postCount Number of PUBLISHED posts using this tag
 * @author Sathira Basnayake
 */
public record PublicBlogTagWithCount(
        BlogTag tag,
        Long postCount
) {
}

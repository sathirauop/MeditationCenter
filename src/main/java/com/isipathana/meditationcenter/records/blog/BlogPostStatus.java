package com.isipathana.meditationcenter.records.blog;

/**
 * Blog post publication status.
 * Defines the lifecycle states of a blog post.
 *
 * @author Sathira Basnayake
 */
public enum BlogPostStatus {
    /**
     * Post is a draft and not visible to the public.
     * Can be edited and auto-saved without validation.
     */
    DRAFT,

    /**
     * Post is published and visible to the public.
     * Must have all required fields (title, content) to reach this state.
     */
    PUBLISHED,

    /**
     * Post is archived and not visible to the public.
     * Previously published posts that are no longer active.
     */
    ARCHIVED
}

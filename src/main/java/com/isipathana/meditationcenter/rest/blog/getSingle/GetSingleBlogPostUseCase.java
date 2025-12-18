package com.isipathana.meditationcenter.rest.blog.getSingle;

import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.blog.BlogPostWithTags;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for fetching a single published blog post by slug.
 * Handles business logic for retrieving post details and incrementing view count.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetSingleBlogPostUseCase {

    private final GetSingleBlogPostDataAccess repository;
    private final GetSingleBlogPostResponseBuilder presenter;

    /**
     * Executes the use case to fetch a published blog post by slug.
     * Increments view count when post is successfully retrieved.
     *
     * @param slug The post slug
     * @return Response DTO with full post details
     * @throws ResourceNotFoundException if post not found or not published
     */
    @Transactional
    public GetSingleBlogPostResponse execute(String slug) {
        log.info("Fetching published blog post by slug: {}", slug);

        BlogPostWithTags post = repository.getPublishedBlogPostBySlug(slug)
                .orElseThrow(() -> {
                    log.warn("Blog post not found or not published: {}", slug);
                    return new ResourceNotFoundException("Blog post not found: " + slug);
                });

        // Increment view count
        repository.incrementViewCount(post.postId());
        log.debug("Incremented view count for post: {}", post.postId());

        // Build response with updated view count
        BlogPostWithTags postWithIncrementedViews = BlogPostWithTags.builder()
                .postId(post.postId())
                .title(post.title())
                .excerpt(post.excerpt())
                .content(post.content())
                .titleSi(post.titleSi())
                .excerptSi(post.excerptSi())
                .contentSi(post.contentSi())
                .slug(post.slug())
                .authorId(post.authorId())
                .authorName(post.authorName())
                .coverImageKey(post.coverImageKey())
                .imageKeys(post.imageKeys())
                .status(post.status())
                .publishedAt(post.publishedAt())
                .metaTitle(post.metaTitle())
                .metaDescription(post.metaDescription())
                .viewCount(post.viewCount() != null ? post.viewCount() + 1 : 1L)
                .tagNames(post.tagNames())
                .version(post.version())
                .createdAt(post.createdAt())
                .updatedAt(post.updatedAt())
                .deletedAt(post.deletedAt())
                .build();

        return presenter.build(postWithIncrementedViews);
    }
}

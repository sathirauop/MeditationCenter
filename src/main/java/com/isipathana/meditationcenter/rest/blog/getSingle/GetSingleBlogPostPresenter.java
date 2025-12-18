package com.isipathana.meditationcenter.rest.blog.getSingle;

import com.isipathana.meditationcenter.records.blog.BlogPostWithTags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Presenter for GET /api/blog/{slug} endpoint.
 * Transforms blog post domain object to API response DTO with presigned image URLs.
 *
 * @author Sathira Basnayake
 */
@Component
public class GetSingleBlogPostPresenter implements GetSingleBlogPostResponseBuilder {

    @Autowired(required = false)
    private GetSingleBlogPostHttpDataAccess httpRepository;

    @Override
    public GetSingleBlogPostResponse build(BlogPostWithTags post) {
        String coverImageUrl = null;
        Set<String> galleryImageUrls = new HashSet<>();

        // Generate presigned URLs if R2 is enabled
        if (httpRepository != null) {
            // Cover image
            if (post.coverImageKey() != null) {
                coverImageUrl = httpRepository.generatePresignedUrl(post.coverImageKey());
            }

            // Gallery images
            if (post.imageKeys() != null && !post.imageKeys().isEmpty()) {
                Map<String, String> urlMap = httpRepository.generatePresignedUrls(post.imageKeys());
                galleryImageUrls = new HashSet<>(urlMap.values());
            }
        }

        return GetSingleBlogPostResponse.builder()
                .postId(post.postId())
                .title(post.title())
                .excerpt(post.excerpt())
                .content(post.content())
                .titleSi(post.titleSi())
                .excerptSi(post.excerptSi())
                .contentSi(post.contentSi())
                .slug(post.slug())
                .authorName(post.authorName())
                .coverImageUrl(coverImageUrl)
                .galleryImageUrls(galleryImageUrls)
                .publishedAt(post.publishedAt())
                .viewCount(post.viewCount())
                .tagNames(post.tagNames())
                .metaTitle(post.metaTitle())
                .metaDescription(post.metaDescription())
                .build();
    }
}

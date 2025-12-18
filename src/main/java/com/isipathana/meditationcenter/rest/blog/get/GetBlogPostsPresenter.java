package com.isipathana.meditationcenter.rest.blog.get;

import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.records.blog.BlogPostWithTags;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

/**
 * Presenter for GET /api/blog endpoint.
 * Transforms blog post domain objects to API response DTOs with presigned image URLs.
 *
 * @author Sathira Basnayake
 */
@Component
@RequiredArgsConstructor
public class GetBlogPostsPresenter implements GetBlogPostsResponseBuilder {

    private final OffsetSearchResponse.Factory responseFactory;

    @Autowired(required = false)
    private GetBlogPostsHttpDataAccess httpRepository;

    @Override
    public OffsetSearchResponse<GetBlogPostsResponse> build(
            Stream<BlogPostWithTags> posts,
            long currentOffset,
            long maxOffset
    ) {
        List<GetBlogPostsResponse> responseList = posts
                .map(this::mapPostToResponse)
                .toList();

        return responseFactory.create(responseList, currentOffset, maxOffset);
    }

    /**
     * Maps a single blog post to response DTO.
     * Generates presigned URL for cover image if R2 is enabled.
     */
    private GetBlogPostsResponse mapPostToResponse(BlogPostWithTags post) {
        String coverImageUrl = null;

        // Generate presigned URL if R2 is enabled and cover image exists
        if (httpRepository != null && post.coverImageKey() != null) {
            coverImageUrl = httpRepository.generatePresignedUrl(post.coverImageKey());
        }

        return GetBlogPostsResponse.builder()
                .postId(post.postId())
                .title(post.title())
                .excerpt(post.excerpt())
                .titleSi(post.titleSi())
                .excerptSi(post.excerptSi())
                .slug(post.slug())
                .authorName(post.authorName())
                .coverImageUrl(coverImageUrl)
                .publishedAt(post.publishedAt())
                .viewCount(post.viewCount())
                .tagNames(post.tagNames())
                .build();
    }
}

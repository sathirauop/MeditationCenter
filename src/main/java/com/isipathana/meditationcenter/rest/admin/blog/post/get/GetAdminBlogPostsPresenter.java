package com.isipathana.meditationcenter.rest.admin.blog.post.get;

import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.records.blog.BlogPostWithTags;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

/**
 * Presenter for GET /api/admin/blog endpoint.
 * Transforms blog post domain objects to admin response DTOs.
 *
 * @author Sathira Basnayake
 */
@Component
@RequiredArgsConstructor
public class GetAdminBlogPostsPresenter implements GetAdminBlogPostsResponseBuilder {

    private final OffsetSearchResponse.Factory responseFactory;

    @Override
    public OffsetSearchResponse<GetAdminBlogPostsResponse> build(
            Stream<BlogPostWithTags> posts,
            long currentOffset,
            long maxOffset
    ) {
        List<GetAdminBlogPostsResponse> responseList = posts
                .map(post -> GetAdminBlogPostsResponse.builder()
                        .postId(post.postId())
                        .title(post.title())
                        .titleSi(post.titleSi())
                        .slug(post.slug())
                        .authorId(post.authorId())
                        .authorName(post.authorName())
                        .status(post.status())
                        .publishedAt(post.publishedAt())
                        .viewCount(post.viewCount())
                        .tagNames(post.tagNames())
                        .createdAt(post.createdAt())
                        .updatedAt(post.updatedAt())
                        .build()
                )
                .toList();

        return responseFactory.create(responseList, currentOffset, maxOffset);
    }
}

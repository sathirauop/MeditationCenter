package com.isipathana.meditationcenter.rest.admin.blog.post.getSingle;

import com.isipathana.meditationcenter.records.blog.BlogPost;
import org.springframework.stereotype.Component;

/**
 * Presenter for GET /api/admin/blog/{postId} endpoint.
 * Transforms blog post domain object to admin response DTO.
 *
 * @author Sathira Basnayake
 */
@Component
public class GetAdminBlogPostPresenter implements GetAdminBlogPostResponseBuilder {

    @Override
    public GetAdminBlogPostResponse build(BlogPost post) {
        return GetAdminBlogPostResponse.builder()
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
                .galleryImageKeys(post.imageKeys())
                .status(post.status())
                .publishedAt(post.publishedAt())
                .metaTitle(post.metaTitle())
                .metaDescription(post.metaDescription())
                .viewCount(post.viewCount())
                .tagIds(post.tagIds())
                .version(post.version())
                .createdAt(post.createdAt())
                .updatedAt(post.updatedAt())
                .build();
    }
}

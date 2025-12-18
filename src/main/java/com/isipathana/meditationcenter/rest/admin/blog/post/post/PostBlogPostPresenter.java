package com.isipathana.meditationcenter.rest.admin.blog.post.post;

import com.isipathana.meditationcenter.records.blog.BlogPost;
import org.springframework.stereotype.Component;

/**
 * Presenter for blog post creation response.
 *
 * @author Sathira Basnayake
 */
@Component
public class PostBlogPostPresenter {

    public PostBlogPostResponse build(BlogPost post) {
        return PostBlogPostResponse.builder()
                .postId(post.postId())
                .title(post.title())
                .titleSi(post.titleSi())
                .slug(post.slug())
                .excerpt(post.excerpt())
                .excerptSi(post.excerptSi())
                .authorId(post.authorId())
                .coverImageKey(post.coverImageKey())
                .galleryImageKeys(post.imageKeys())
                .status(post.status())
                .publishedAt(post.publishedAt())
                .tagIds(post.tagIds())
                .createdAt(post.createdAt())
                .updatedAt(post.updatedAt())
                .build();
    }
}

package com.isipathana.meditationcenter.rest.admin.blog.tag.post;

import com.isipathana.meditationcenter.records.blog.BlogTag;
import org.springframework.stereotype.Component;

/**
 * Presenter for blog tag creation response.
 * Transforms domain BlogTag to PostBlogTagResponse DTO.
 *
 * @author Sathira Basnayake
 */
@Component
public class PostBlogTagPresenter implements PostBlogTagResponseBuilder {

    @Override
    public PostBlogTagResponse build(BlogTag tag) {
        return PostBlogTagResponse.builder()
                .tagId(tag.tagId())
                .name(tag.name())
                .nameSi(tag.nameSi())
                .slug(tag.slug())
                .createdAt(tag.createdAt())
                .build();
    }
}

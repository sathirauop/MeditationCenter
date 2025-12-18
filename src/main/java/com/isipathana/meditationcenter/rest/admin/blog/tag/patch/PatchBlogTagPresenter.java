package com.isipathana.meditationcenter.rest.admin.blog.tag.patch;

import com.isipathana.meditationcenter.records.blog.BlogTag;
import org.springframework.stereotype.Component;

/**
 * Presenter for blog tag update response.
 *
 * @author Sathira Basnayake
 */
@Component
public class PatchBlogTagPresenter {

    public PatchBlogTagResponse build(BlogTag tag) {
        return PatchBlogTagResponse.builder()
                .tagId(tag.tagId())
                .name(tag.name())
                .nameSi(tag.nameSi())
                .slug(tag.slug())
                .build();
    }
}

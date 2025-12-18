package com.isipathana.meditationcenter.rest.blog.getTags;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

/**
 * Presenter for GET /api/blog/tags endpoint.
 * Transforms tag domain objects to API response DTOs.
 *
 * @author Sathira Basnayake
 */
@Component
public class GetPublicBlogTagsPresenter implements GetPublicBlogTagsResponseBuilder {

    @Override
    public List<GetPublicBlogTagsResponse> build(Stream<PublicBlogTagWithCount> tags) {
        return tags
                .map(tagWithCount -> GetPublicBlogTagsResponse.builder()
                        .tagId(tagWithCount.tag().tagId())
                        .name(tagWithCount.tag().name())
                        .nameSi(tagWithCount.tag().nameSi())
                        .slug(tagWithCount.tag().slug())
                        .postCount(tagWithCount.postCount())
                        .build()
                )
                .toList();
    }
}

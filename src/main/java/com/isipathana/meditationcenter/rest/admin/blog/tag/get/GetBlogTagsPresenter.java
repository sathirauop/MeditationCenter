package com.isipathana.meditationcenter.rest.admin.blog.tag.get;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

/**
 * Presenter for blog tags list response.
 * Transforms blog tags with counts to response DTOs.
 *
 * @author Sathira Basnayake
 */
@Component
public class GetBlogTagsPresenter implements GetBlogTagsResponseBuilder {

    @Override
    public List<GetBlogTagsResponse> build(Stream<BlogTagWithCount> tagsWithCount) {
        return tagsWithCount
                .map(tagWithCount -> GetBlogTagsResponse.builder()
                        .tagId(tagWithCount.tag().tagId())
                        .name(tagWithCount.tag().name())
                        .nameSi(tagWithCount.tag().nameSi())
                        .slug(tagWithCount.tag().slug())
                        .postCount(tagWithCount.postCount())
                        .createdAt(tagWithCount.tag().createdAt())
                        .build())
                .toList();
    }
}

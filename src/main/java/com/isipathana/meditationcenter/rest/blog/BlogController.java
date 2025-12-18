package com.isipathana.meditationcenter.rest.blog;

import com.isipathana.meditationcenter.constants.EndPoints;
import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.rest.blog.get.GetBlogPostsRequest;
import com.isipathana.meditationcenter.rest.blog.get.GetBlogPostsResponse;
import com.isipathana.meditationcenter.rest.blog.get.GetBlogPostsUseCase;
import com.isipathana.meditationcenter.rest.blog.getSingle.GetSingleBlogPostResponse;
import com.isipathana.meditationcenter.rest.blog.getSingle.GetSingleBlogPostUseCase;
import com.isipathana.meditationcenter.rest.blog.getTags.GetPublicBlogTagsResponse;
import com.isipathana.meditationcenter.rest.blog.getTags.GetPublicBlogTagsUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for public blog endpoints.
 * PUBLIC - No authentication required.
 *
 * @author Sathira Basnayake
 */
@RestController
@RequestMapping(EndPoints.Blog.BASE)
@RequiredArgsConstructor
public class BlogController {

    private final GetBlogPostsUseCase getBlogPostsUseCase;
    private final GetSingleBlogPostUseCase getSingleBlogPostUseCase;
    private final GetPublicBlogTagsUseCase getPublicBlogTagsUseCase;

    /**
     * GET /api/blog - List published blog posts (public, paginated, filterable)
     */
    @GetMapping(EndPoints.Blog.GET_ALL)
    public ResponseEntity<OffsetSearchResponse<GetBlogPostsResponse>> getBlogPosts(
            @Valid GetBlogPostsRequest request
    ) {
        return ResponseEntity.ok(getBlogPostsUseCase.execute(request));
    }

    /**
     * GET /api/blog/{slug} - Get single published blog post by slug (public)
     */
    @GetMapping(EndPoints.Blog.GET_BY_SLUG)
    public ResponseEntity<GetSingleBlogPostResponse> getBlogPostBySlug(
            @PathVariable String slug
    ) {
        return ResponseEntity.ok(getSingleBlogPostUseCase.execute(slug));
    }

    /**
     * GET /api/blog/tags - Get all tags with published post counts (public)
     */
    @GetMapping(EndPoints.Blog.GET_TAGS)
    public ResponseEntity<List<GetPublicBlogTagsResponse>> getBlogTags() {
        return ResponseEntity.ok(getPublicBlogTagsUseCase.execute());
    }
}

package com.isipathana.meditationcenter.rest.admin.blog.post;

import com.isipathana.meditationcenter.constants.EndPoints;
import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.rest.admin.blog.post.delete.DeleteBlogPostUseCase;
import com.isipathana.meditationcenter.rest.admin.blog.post.get.GetAdminBlogPostsRequest;
import com.isipathana.meditationcenter.rest.admin.blog.post.get.GetAdminBlogPostsResponse;
import com.isipathana.meditationcenter.rest.admin.blog.post.get.GetAdminBlogPostsUseCase;
import com.isipathana.meditationcenter.rest.admin.blog.post.getSingle.GetAdminBlogPostResponse;
import com.isipathana.meditationcenter.rest.admin.blog.post.getSingle.GetAdminBlogPostUseCase;
import com.isipathana.meditationcenter.rest.admin.blog.post.post.PostBlogPostRequest;
import com.isipathana.meditationcenter.rest.admin.blog.post.post.PostBlogPostResponse;
import com.isipathana.meditationcenter.rest.admin.blog.post.post.PostBlogPostUseCase;
import com.isipathana.meditationcenter.rest.admin.blog.post.publish.PublishBlogPostUseCase;
import com.isipathana.meditationcenter.rest.admin.blog.post.unpublish.UnpublishBlogPostUseCase;
import com.isipathana.meditationcenter.security.MeditationCenterUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST controller for admin blog post management.
 * ADMIN only - requires blog post permissions.
 *
 * @author Sathira Basnayake
 */
@RestController
@RequestMapping(EndPoints.Admin.Blog.BASE)
@RequiredArgsConstructor
public class AdminBlogController {

    private final GetAdminBlogPostsUseCase getAdminBlogPostsUseCase;
    private final GetAdminBlogPostUseCase getAdminBlogPostUseCase;
    private final PostBlogPostUseCase postBlogPostUseCase;
    private final PublishBlogPostUseCase publishBlogPostUseCase;
    private final UnpublishBlogPostUseCase unpublishBlogPostUseCase;
    private final DeleteBlogPostUseCase deleteBlogPostUseCase;

    /**
     * GET /api/admin/blog - Get all blog posts including drafts (admin)
     */
    @GetMapping(EndPoints.Admin.Blog.GET_ALL)
    @PreAuthorize("hasAuthority('VIEW_ALL_BLOG_POSTS')")
    public ResponseEntity<OffsetSearchResponse<GetAdminBlogPostsResponse>> getAllBlogPosts(
            @Valid GetAdminBlogPostsRequest request
    ) {
        return ResponseEntity.ok(getAdminBlogPostsUseCase.execute(request));
    }

    /**
     * GET /api/admin/blog/{postId} - Get blog post by ID for editing
     */
    @GetMapping(EndPoints.Admin.Blog.GET_BY_ID)
    @PreAuthorize("hasAuthority('VIEW_ALL_BLOG_POSTS')")
    public ResponseEntity<GetAdminBlogPostResponse> getBlogPostById(
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(getAdminBlogPostUseCase.execute(postId));
    }

    /**
     * POST /api/admin/blog - Create blog post with multipart/form-data
     */
    @PostMapping(EndPoints.Admin.Blog.CREATE)
    @PreAuthorize("hasAuthority('CREATE_BLOG_POST')")
    public ResponseEntity<PostBlogPostResponse> createBlogPost(
            @Valid @RequestPart("request") PostBlogPostRequest request,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
            @RequestPart(value = "galleryImages", required = false) List<MultipartFile> galleryImages,
            @AuthenticationPrincipal MeditationCenterUser user
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postBlogPostUseCase.execute(request, user.getUserId(), coverImage, galleryImages));
    }

    /**
     * POST /api/admin/blog/{postId}/publish - Publish draft post
     */
    @PostMapping(EndPoints.Admin.Blog.PUBLISH)
    @PreAuthorize("hasAuthority('PUBLISH_BLOG_POST')")
    public ResponseEntity<Void> publishPost(@PathVariable Long postId) {
        publishBlogPostUseCase.execute(postId);
        return ResponseEntity.ok().build();
    }

    /**
     * POST /api/admin/blog/{postId}/unpublish - Unpublish post (revert to draft)
     */
    @PostMapping(EndPoints.Admin.Blog.UNPUBLISH)
    @PreAuthorize("hasAuthority('PUBLISH_BLOG_POST')")
    public ResponseEntity<Void> unpublishPost(@PathVariable Long postId) {
        unpublishBlogPostUseCase.execute(postId);
        return ResponseEntity.ok().build();
    }

    /**
     * DELETE /api/admin/blog/{postId} - Soft delete blog post
     */
    @DeleteMapping(EndPoints.Admin.Blog.DELETE)
    @PreAuthorize("hasAuthority('DELETE_BLOG_POST')")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        deleteBlogPostUseCase.execute(postId);
        return ResponseEntity.noContent().build();
    }
}

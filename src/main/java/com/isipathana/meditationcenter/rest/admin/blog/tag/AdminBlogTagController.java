package com.isipathana.meditationcenter.rest.admin.blog.tag;

import com.isipathana.meditationcenter.constants.EndPoints;
import com.isipathana.meditationcenter.rest.admin.blog.tag.delete.DeleteBlogTagUseCase;
import com.isipathana.meditationcenter.rest.admin.blog.tag.get.GetBlogTagsResponse;
import com.isipathana.meditationcenter.rest.admin.blog.tag.get.GetBlogTagsUseCase;
import com.isipathana.meditationcenter.rest.admin.blog.tag.patch.PatchBlogTagRequest;
import com.isipathana.meditationcenter.rest.admin.blog.tag.patch.PatchBlogTagResponse;
import com.isipathana.meditationcenter.rest.admin.blog.tag.patch.PatchBlogTagUseCase;
import com.isipathana.meditationcenter.rest.admin.blog.tag.post.PostBlogTagRequest;
import com.isipathana.meditationcenter.rest.admin.blog.tag.post.PostBlogTagResponse;
import com.isipathana.meditationcenter.rest.admin.blog.tag.post.PostBlogTagUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for admin blog tag management.
 * ADMIN only - requires MANAGE_BLOG_TAGS permission.
 *
 * @author Sathira Basnayake
 */
@RestController
@RequestMapping(EndPoints.Admin.Blog.BASE)
@RequiredArgsConstructor
public class AdminBlogTagController {

    private final PostBlogTagUseCase postBlogTagUseCase;
    private final GetBlogTagsUseCase getBlogTagsUseCase;
    private final PatchBlogTagUseCase patchBlogTagUseCase;
    private final DeleteBlogTagUseCase deleteBlogTagUseCase;

    /**
     * GET /api/admin/blog/tags - Get all tags
     */
    @GetMapping(EndPoints.Admin.Blog.Tags.GET_ALL)
    @PreAuthorize("hasAuthority('MANAGE_BLOG_TAGS')")
    public ResponseEntity<List<GetBlogTagsResponse>> getAllTags() {
        return ResponseEntity.ok(getBlogTagsUseCase.execute());
    }

    /**
     * POST /api/admin/blog/tags - Create new tag
     */
    @PostMapping(EndPoints.Admin.Blog.Tags.CREATE)
    @PreAuthorize("hasAuthority('MANAGE_BLOG_TAGS')")
    public ResponseEntity<PostBlogTagResponse> createTag(@Valid @RequestBody PostBlogTagRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postBlogTagUseCase.execute(request));
    }

    /**
     * PATCH /api/admin/blog/tags/{tagId} - Update tag
     */
    @PatchMapping(EndPoints.Admin.Blog.Tags.UPDATE)
    @PreAuthorize("hasAuthority('MANAGE_BLOG_TAGS')")
    public ResponseEntity<PatchBlogTagResponse> updateTag(
            @PathVariable Long tagId,
            @Valid @RequestBody PatchBlogTagRequest request
    ) {
        return ResponseEntity.ok(patchBlogTagUseCase.execute(tagId, request));
    }

    /**
     * DELETE /api/admin/blog/tags/{tagId} - Delete tag
     */
    @DeleteMapping(EndPoints.Admin.Blog.Tags.DELETE)
    @PreAuthorize("hasAuthority('MANAGE_BLOG_TAGS')")
    public ResponseEntity<Void> deleteTag(@PathVariable Long tagId) {
        deleteBlogTagUseCase.execute(tagId);
        return ResponseEntity.noContent().build();
    }
}

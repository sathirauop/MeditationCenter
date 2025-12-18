package com.isipathana.meditationcenter.rest.admin.blog.post.post;

import com.isipathana.meditationcenter.exception.ValidationException;
import com.isipathana.meditationcenter.records.blog.BlogPost;
import com.isipathana.meditationcenter.records.blog.BlogPostStatus;
import com.isipathana.meditationcenter.util.SlugGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * UseCase for creating blog posts.
 * Handles slug generation, post creation, image uploads, and tag associations.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostBlogPostUseCase {

    private final PostBlogPostDataAccess repository;
    private final PostBlogPostPresenter presenter;
    private final SlugGenerator slugGenerator;

    @Autowired(required = false)
    private PostBlogPostHttpDataAccess httpRepository;

    /**
     * Creates a new blog post with optional image uploads.
     *
     * @param request       The blog post creation request
     * @param authorId      The author's user ID (from authentication)
     * @param coverImage    Optional cover image
     * @param galleryImages Optional gallery images
     * @return The created blog post response
     */
    @Transactional
    public PostBlogPostResponse execute(
            PostBlogPostRequest request,
            Long authorId,
            MultipartFile coverImage,
            List<MultipartFile> galleryImages
    ) {
        log.info("Creating new blog post: {}", request.title());

        // Generate or validate slug
        String slug;
        if (request.slug() != null && !request.slug().isBlank()) {
            slug = request.slug();
            if (!slugGenerator.isValid(slug)) {
                throw new ValidationException("Invalid slug format: " + slug);
            }
        } else {
            slug = slugGenerator.generate(request.title());
        }

        // Ensure slug is unique
        slug = slugGenerator.ensureUnique(slug, repository::existsBySlug);

        // Determine status (default to DRAFT)
        BlogPostStatus status = request.status() != null ? request.status() : BlogPostStatus.DRAFT;

        // Build blog post domain object
        BlogPost postToCreate = BlogPost.builder()
                .title(request.title())
                .excerpt(request.excerpt())
                .content(request.content())
                .titleSi(request.titleSi())
                .excerptSi(request.excerptSi())
                .contentSi(request.contentSi())
                .slug(slug)
                .authorId(authorId)
                .status(status)
                .metaTitle(request.metaTitle())
                .metaDescription(request.metaDescription())
                .tagIds(request.tagIds())
                .build();

        // Create blog post
        BlogPost createdPost = repository.createBlogPost(postToCreate);
        log.info("Blog post created with ID: {}", createdPost.postId());

        // Upload images if R2 is enabled
        String coverImageKey = null;
        Set<String> galleryImageKeys = new HashSet<>();

        if (httpRepository != null) {
            if (coverImage != null && !coverImage.isEmpty()) {
                log.info("Uploading cover image for post {}", createdPost.postId());
                coverImageKey = httpRepository.uploadCoverImage(createdPost.postId(), coverImage);
            }

            if (galleryImages != null && !galleryImages.isEmpty()) {
                log.info("Uploading {} gallery images for post {}", galleryImages.size(), createdPost.postId());
                List<String> uploadedKeys = httpRepository.uploadGalleryImages(createdPost.postId(), galleryImages);
                galleryImageKeys.addAll(uploadedKeys);
            }

            // Update post with image keys
            if (coverImageKey != null || !galleryImageKeys.isEmpty()) {
                log.info("Updating post {} with image keys", createdPost.postId());
                createdPost = repository.updateImageKeys(
                        createdPost.postId(),
                        coverImageKey,
                        galleryImageKeys.isEmpty() ? null : galleryImageKeys
                );
            }
        }

        // Associate tags
        if (request.tagIds() != null && !request.tagIds().isEmpty()) {
            log.info("Associating {} tags with post {}", request.tagIds().size(), createdPost.postId());
            repository.associateTags(createdPost.postId(), request.tagIds());
        }

        log.info("Blog post creation completed successfully: {}", createdPost.postId());

        return presenter.build(createdPost);
    }

    /**
     * Creates a blog post without images (JSON only, backward compatibility).
     *
     * @param request  The blog post creation request
     * @param authorId The author's user ID
     * @return The created blog post response
     */
    @Transactional
    public PostBlogPostResponse execute(PostBlogPostRequest request, Long authorId) {
        return execute(request, authorId, null, null);
    }
}

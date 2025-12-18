package com.isipathana.meditationcenter.rest.admin.blog.tag.post;

import com.isipathana.meditationcenter.exception.ValidationException;
import com.isipathana.meditationcenter.records.blog.BlogTag;
import com.isipathana.meditationcenter.util.SlugGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for creating new blog tags.
 * Handles slug generation, uniqueness validation, and tag creation.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostBlogTagUseCase {

    private final PostBlogTagDataAccess repository;
    private final PostBlogTagResponseBuilder presenter;
    private final SlugGenerator slugGenerator;

    /**
     * Creates a new blog tag.
     *
     * @param request The tag creation request
     * @return The created tag response
     * @throws ValidationException if tag name or slug already exists
     */
    @Transactional
    public PostBlogTagResponse execute(PostBlogTagRequest request) {
        log.info("Creating new blog tag: {}", request.name());

        // Check if tag name already exists
        if (repository.existsByName(request.name())) {
            throw new ValidationException("Tag with name '" + request.name() + "' already exists");
        }

        // Generate or validate slug
        String slug;
        if (request.slug() != null && !request.slug().isBlank()) {
            // User provided slug - validate it
            slug = request.slug();
            if (!slugGenerator.isValid(slug)) {
                throw new ValidationException("Invalid slug format: " + slug);
            }
        } else {
            // Auto-generate slug from name
            slug = slugGenerator.generate(request.name());
        }

        // Ensure slug is unique (append counter if needed)
        slug = slugGenerator.ensureUnique(slug, repository::existsBySlug);

        // Build domain object
        BlogTag tagToCreate = BlogTag.builder()
                .name(request.name())
                .nameSi(request.nameSi())
                .slug(slug)
                .build();

        // Create tag
        BlogTag createdTag = repository.createTag(tagToCreate);

        log.info("Blog tag created successfully with ID: {} and slug: {}", createdTag.tagId(), createdTag.slug());

        // Build and return response
        return presenter.build(createdTag);
    }
}

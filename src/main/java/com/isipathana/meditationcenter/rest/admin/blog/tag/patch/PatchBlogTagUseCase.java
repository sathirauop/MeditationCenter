package com.isipathana.meditationcenter.rest.admin.blog.tag.patch;

import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.blog.BlogTag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for updating blog tags.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatchBlogTagUseCase {

    private final PatchBlogTagDataAccess repository;
    private final PatchBlogTagPresenter presenter;

    @Transactional
    public PatchBlogTagResponse execute(Long tagId, PatchBlogTagRequest request) {
        log.info("Updating blog tag ID: {}", tagId);

        // Verify tag exists
        repository.findTagById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog tag not found with ID: " + tagId));

        // Build partial update
        BlogTag tagToUpdate = BlogTag.builder()
                .tagId(tagId)
                .name(request.name())
                .nameSi(request.nameSi())
                .build();

        // Update
        BlogTag updatedTag = repository.updateTag(tagToUpdate);

        log.info("Blog tag updated successfully: {}", updatedTag.tagId());

        return presenter.build(updatedTag);
    }
}

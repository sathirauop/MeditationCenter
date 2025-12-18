package com.isipathana.meditationcenter.rest.admin.blog.tag.delete;

import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for deleting blog tags.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteBlogTagUseCase {

    private final DeleteBlogTagDataAccess repository;

    @Transactional
    public void execute(Long tagId) {
        log.info("Deleting blog tag ID: {}", tagId);

        if (!repository.existsById(tagId)) {
            throw new ResourceNotFoundException("Blog tag not found with ID: " + tagId);
        }

        repository.deleteTag(tagId);

        log.info("Blog tag deleted successfully: {}", tagId);
    }
}

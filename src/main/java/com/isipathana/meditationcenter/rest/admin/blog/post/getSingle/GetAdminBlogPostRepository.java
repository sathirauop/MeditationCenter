package com.isipathana.meditationcenter.rest.admin.blog.post.getSingle;

import com.isipathana.meditationcenter.jooq.tables.records.BlogPostsRecord;
import com.isipathana.meditationcenter.records.blog.BlogPost;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.isipathana.meditationcenter.jooq.Tables.*;

/**
 * Repository for fetching a single blog post for admin editing.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class GetAdminBlogPostRepository implements GetAdminBlogPostDataAccess {

    private final DSLContext dslContext;

    @Override
    public Optional<BlogPost> getBlogPostById(Long postId) {
        BlogPostsRecord record = dslContext
                .selectFrom(BLOG_POSTS)
                .where(BLOG_POSTS.POST_ID.eq(postId))
                .and(BLOG_POSTS.DELETED_AT.isNull())
                .fetchOne();

        if (record == null) {
            return Optional.empty();
        }

        // Fetch author name
        String authorName = dslContext
                .select(USERS.NAME)
                .from(USERS)
                .where(USERS.USER_ID.eq(record.getAuthorId()))
                .fetchOne(USERS.NAME);

        // Fetch tag IDs
        Set<Long> tagIds = new HashSet<>(
                dslContext
                        .select(BLOG_POST_TAGS.TAG_ID)
                        .from(BLOG_POST_TAGS)
                        .where(BLOG_POST_TAGS.POST_ID.eq(postId))
                        .fetchSet(BLOG_POST_TAGS.TAG_ID)
        );

        return Optional.of(BlogPost.builder()
                .postId(record.getPostId())
                .title(record.getTitle())
                .excerpt(record.getExcerpt())
                .content(record.getContent())
                .titleSi(record.getTitleSi())
                .excerptSi(record.getExcerptSi())
                .contentSi(record.getContentSi())
                .slug(record.getSlug())
                .authorId(record.getAuthorId())
                .authorName(authorName)
                .coverImageKey(record.getCoverImageKey())
                .imageKeys(record.getImageKeys() != null ?
                        new HashSet<>(Arrays.asList(record.getImageKeys())) : new HashSet<>())
                .status(record.getStatus())
                .publishedAt(record.getPublishedAt())
                .metaTitle(record.getMetaTitle())
                .metaDescription(record.getMetaDescription())
                .viewCount(record.getViewCount())
                .tagIds(tagIds)
                .version(record.getVersion())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .deletedAt(record.getDeletedAt())
                .build());
    }
}

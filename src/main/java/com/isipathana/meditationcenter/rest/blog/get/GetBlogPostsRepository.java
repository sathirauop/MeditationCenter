package com.isipathana.meditationcenter.rest.blog.get;

import com.isipathana.meditationcenter.records.blog.BlogPostStatus;
import com.isipathana.meditationcenter.records.blog.BlogPostWithTags;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.OrderField;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import static com.isipathana.meditationcenter.jooq.Tables.*;
import static org.jooq.impl.DSL.arrayAgg;
import static org.jooq.impl.DSL.count;

/**
 * Repository for fetching published blog posts.
 * Uses jOOQ for type-safe database queries with complex filtering and aggregation.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class GetBlogPostsRepository implements GetBlogPostsDataAccess {

    private final DSLContext dslContext;

    @Override
    public Stream<BlogPostWithTags> getPublishedBlogPosts(GetBlogPostsRequest request) {
        // Base conditions
        List<Condition> conditions = buildConditions(request);

        // Determine sort order
        List<OrderField<?>> orderFields = switch (request.sortBy()) {
            case OLDEST -> List.of(BLOG_POSTS.PUBLISHED_AT.asc());
            case MOST_VIEWED -> List.of(
                    BLOG_POSTS.VIEW_COUNT.desc().nullsLast(),
                    BLOG_POSTS.PUBLISHED_AT.desc()
            );
            default -> List.of(BLOG_POSTS.PUBLISHED_AT.desc()); // NEWEST
        };

        return dslContext
                .select(
                        BLOG_POSTS.POST_ID,
                        BLOG_POSTS.TITLE,
                        BLOG_POSTS.EXCERPT,
                        BLOG_POSTS.CONTENT,
                        BLOG_POSTS.TITLE_SI,
                        BLOG_POSTS.EXCERPT_SI,
                        BLOG_POSTS.CONTENT_SI,
                        BLOG_POSTS.SLUG,
                        BLOG_POSTS.AUTHOR_ID,
                        USERS.NAME.as("author_name"),
                        BLOG_POSTS.COVER_IMAGE_KEY,
                        BLOG_POSTS.IMAGE_KEYS,
                        BLOG_POSTS.STATUS,
                        BLOG_POSTS.PUBLISHED_AT,
                        BLOG_POSTS.META_TITLE,
                        BLOG_POSTS.META_DESCRIPTION,
                        BLOG_POSTS.VIEW_COUNT,
                        arrayAgg(BLOG_TAGS.NAME).filterWhere(BLOG_TAGS.NAME.isNotNull()).as("tag_names"),
                        BLOG_POSTS.VERSION,
                        BLOG_POSTS.CREATED_AT,
                        BLOG_POSTS.UPDATED_AT,
                        BLOG_POSTS.DELETED_AT
                )
                .from(BLOG_POSTS)
                .join(USERS).on(BLOG_POSTS.AUTHOR_ID.eq(USERS.USER_ID))
                .leftJoin(BLOG_POST_TAGS).on(BLOG_POSTS.POST_ID.eq(BLOG_POST_TAGS.POST_ID))
                .leftJoin(BLOG_TAGS).on(BLOG_POST_TAGS.TAG_ID.eq(BLOG_TAGS.TAG_ID))
                .where(conditions)
                .groupBy(
                        BLOG_POSTS.POST_ID,
                        BLOG_POSTS.TITLE,
                        BLOG_POSTS.EXCERPT,
                        BLOG_POSTS.CONTENT,
                        BLOG_POSTS.TITLE_SI,
                        BLOG_POSTS.EXCERPT_SI,
                        BLOG_POSTS.CONTENT_SI,
                        BLOG_POSTS.SLUG,
                        BLOG_POSTS.AUTHOR_ID,
                        USERS.NAME,
                        BLOG_POSTS.COVER_IMAGE_KEY,
                        BLOG_POSTS.IMAGE_KEYS,
                        BLOG_POSTS.STATUS,
                        BLOG_POSTS.PUBLISHED_AT,
                        BLOG_POSTS.META_TITLE,
                        BLOG_POSTS.META_DESCRIPTION,
                        BLOG_POSTS.VIEW_COUNT,
                        BLOG_POSTS.VERSION,
                        BLOG_POSTS.CREATED_AT,
                        BLOG_POSTS.UPDATED_AT,
                        BLOG_POSTS.DELETED_AT
                )
                .orderBy(orderFields)
                .limit(request.limit())
                .offset(request.offset())
                .fetchStream()
                .map(record -> BlogPostWithTags.builder()
                        .postId(record.value1())
                        .title(record.value2())
                        .excerpt(record.value3())
                        .content(record.value4())
                        .titleSi(record.value5())
                        .excerptSi(record.value6())
                        .contentSi(record.value7())
                        .slug(record.value8())
                        .authorId(record.value9())
                        .authorName(record.value10())
                        .coverImageKey(record.value11())
                        .imageKeys(record.value12() != null ?
                                new HashSet<>(Arrays.asList(record.value12())) : new HashSet<>())
                        .status(record.value13())
                        .publishedAt(record.value14())
                        .metaTitle(record.value15())
                        .metaDescription(record.value16())
                        .viewCount(record.value17())
                        .tagNames(record.value18() != null ?
                                new HashSet<>(Arrays.asList(record.value18())) : new HashSet<>())
                        .version(record.value19())
                        .createdAt(record.value20())
                        .updatedAt(record.value21())
                        .deletedAt(record.value22())
                        .build()
                );
    }

    @Override
    public long countPublishedBlogPosts(GetBlogPostsRequest request) {
        List<Condition> conditions = buildConditions(request);

        // If filtering by tags, need distinct count
        if (request.tagIds() != null && !request.tagIds().isEmpty()) {
            return dslContext
                    .selectCount()
                    .from(
                            dslContext.selectDistinct(BLOG_POSTS.POST_ID)
                                    .from(BLOG_POSTS)
                                    .leftJoin(BLOG_POST_TAGS).on(BLOG_POSTS.POST_ID.eq(BLOG_POST_TAGS.POST_ID))
                                    .where(conditions)
                    )
                    .fetchOne(0, Long.class);
        }

        return dslContext
                .selectCount()
                .from(BLOG_POSTS)
                .where(conditions)
                .fetchOne(0, Long.class);
    }

    /**
     * Builds WHERE conditions based on request filters.
     */
    private List<Condition> buildConditions(GetBlogPostsRequest request) {
        List<Condition> conditions = new ArrayList<>();

        // Only published posts
        conditions.add(BLOG_POSTS.STATUS.eq(BlogPostStatus.PUBLISHED));

        // Not deleted
        conditions.add(BLOG_POSTS.DELETED_AT.isNull());

        // Filter by tags (posts must have ALL specified tags)
        if (request.tagIds() != null && !request.tagIds().isEmpty()) {
            conditions.add(BLOG_POST_TAGS.TAG_ID.in(request.tagIds()));
        }

        // Search in title or content (case-insensitive)
        if (request.search() != null && !request.search().isBlank()) {
            String searchPattern = "%" + request.search().toLowerCase() + "%";
            conditions.add(
                    DSL.lower(BLOG_POSTS.TITLE).like(searchPattern)
                            .or(DSL.lower(BLOG_POSTS.CONTENT).like(searchPattern))
            );
        }

        // Date range filters
        if (request.startDate() != null) {
            conditions.add(BLOG_POSTS.PUBLISHED_AT.greaterOrEqual(request.startDate().atStartOfDay()));
        }
        if (request.endDate() != null) {
            conditions.add(BLOG_POSTS.PUBLISHED_AT.lessThan(request.endDate().plusDays(1).atStartOfDay()));
        }

        return conditions;
    }
}

package com.company.scopery.common.pagination;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/**
 * Framework-free paginated result, safe to use in domain repository interfaces
 * (unlike Spring Data's Page). {@link #fromSpringPage} is the adapter used by
 * infrastructure-layer repository implementations to bridge from Spring Data;
 * domain code never calls it.
 */
public record PageResult<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {

    public static <T> PageResult<T> fromSpringPage(Page<T> springPage) {
        return new PageResult<>(
                springPage.getContent(),
                springPage.getNumber(),
                springPage.getSize(),
                springPage.getTotalElements(),
                springPage.getTotalPages(),
                springPage.isFirst(),
                springPage.isLast());
    }

    public <R> PageResult<R> map(Function<T, R> mapper) {
        return new PageResult<>(
                content.stream().map(mapper).toList(),
                page, size, totalElements, totalPages, first, last);
    }
}

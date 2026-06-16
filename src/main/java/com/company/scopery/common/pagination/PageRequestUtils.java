package com.company.scopery.common.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageRequestUtils {

    private static final int MAX_SIZE = 100;

    public static Pageable of(int page, int size) {
        return PageRequest.of(Math.max(0, page), clampSize(size));
    }

    public static Pageable of(int page, int size, Sort sort) {
        return PageRequest.of(Math.max(0, page), clampSize(size), sort);
    }

    private static int clampSize(int size) {
        return Math.min(Math.max(1, size), MAX_SIZE);
    }

    private PageRequestUtils() {}
}

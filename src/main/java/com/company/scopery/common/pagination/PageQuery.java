package com.company.scopery.common.pagination;

/**
 * Framework-free pagination request, safe to use in domain repository interfaces
 * (unlike Spring Data's Pageable). sortBy/ascending are optional; a null sortBy
 * means "let the repository implementation pick its own default order".
 */
public record PageQuery(int page, int size, String sortBy, boolean ascending) {

    private static final int MAX_SIZE = 100;

    public static PageQuery of(int page, int size) {
        return new PageQuery(Math.max(0, page), clampSize(size), null, false);
    }

    public static PageQuery of(int page, int size, String sortBy, boolean ascending) {
        return new PageQuery(Math.max(0, page), clampSize(size), sortBy, ascending);
    }

    private static int clampSize(int size) {
        return Math.min(Math.max(1, size), MAX_SIZE);
    }
}

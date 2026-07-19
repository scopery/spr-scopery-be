package com.company.scopery.modules.productivity.search.application.response;
import java.util.List;
public record SearchPageResponse(List<SearchResultResponse> items, long totalElements, int page, int size) {}

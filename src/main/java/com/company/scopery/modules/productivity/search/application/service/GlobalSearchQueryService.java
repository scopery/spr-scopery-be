package com.company.scopery.modules.productivity.search.application.service;
import com.company.scopery.modules.productivity.search.application.response.*;
import com.company.scopery.modules.productivity.search.infrastructure.persistence.SpringDataSearchIndexJpaRepository;
import com.company.scopery.modules.productivity.shared.activity.ProductivityActivityLogger;
import com.company.scopery.modules.productivity.shared.authorization.ProductivityAuthorizationService;
import com.company.scopery.modules.productivity.shared.constant.*;
import com.company.scopery.modules.trust.shared.domain.SensitiveFieldMasker;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service
public class GlobalSearchQueryService {
    private final SpringDataSearchIndexJpaRepository index;
    private final ProductivityAuthorizationService authorization;
    private final ProductivityActivityLogger activityLogger;
    public GlobalSearchQueryService(SpringDataSearchIndexJpaRepository index, ProductivityAuthorizationService authorization, ProductivityActivityLogger activityLogger) {
        this.index=index; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional(readOnly=true)
    public SearchPageResponse search(UUID workspaceId, String query, int page, int size) {
        authorization.requireSearch(workspaceId);
        String q = query == null ? "" : query.trim();
        var result = index.search(workspaceId, q, PageRequest.of(Math.max(page,0), Math.min(Math.max(size,1),100)));
        List<SearchResultResponse> items = result.getContent().stream().map(e -> {
            String rawTitle = e.getTitle();
            String rawSubtitle = e.getSubtitle();
            String rawSnippet = e.getBodyText() == null ? null
                    : e.getBodyText().substring(0, Math.min(160, e.getBodyText().length()));
            String title = SensitiveFieldMasker.maskSearchText(rawTitle);
            String subtitle = SensitiveFieldMasker.maskSearchText(rawSubtitle);
            String snippet = SensitiveFieldMasker.maskSearchText(rawSnippet);
            boolean masked = SensitiveFieldMasker.changed(rawTitle, title)
                    || SensitiveFieldMasker.changed(rawSubtitle, subtitle)
                    || SensitiveFieldMasker.changed(rawSnippet, snippet);
            return new SearchResultResponse(
                    e.getTargetType(), e.getTargetId(), e.getWorkspaceId(), e.getProjectId(),
                    title, subtitle, snippet,
                    e.getStatus(), e.getUpdatedAt(), List.of("title"), 1.0,
                    "/app/" + e.getTargetType().toLowerCase() + "/" + e.getTargetId(), masked);
        }).toList();
        activityLogger.logSuccess(ProductivityEntityTypes.COMMAND, null, ProductivityActivityActions.SEARCH_EXECUTED, "Search: " + q);
        return new SearchPageResponse(items, result.getTotalElements(), page, size);
    }
    @Transactional(readOnly=true)
    public List<String> scopes(UUID workspaceId) {
        authorization.requireSearch(workspaceId);
        return Arrays.stream(com.company.scopery.modules.productivity.search.domain.enums.SearchScope.values()).map(Enum::name).toList();
    }
}

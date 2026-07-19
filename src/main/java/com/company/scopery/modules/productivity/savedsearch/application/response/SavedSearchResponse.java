package com.company.scopery.modules.productivity.savedsearch.application.response;
import com.company.scopery.modules.productivity.savedsearch.domain.model.SavedSearch;
import java.util.UUID;
public record SavedSearchResponse(UUID id, String name, String scope, String queryText, String status) {
    public static SavedSearchResponse from(SavedSearch s) { return new SavedSearchResponse(s.id(), s.name(), s.scope(), s.queryText(), s.status()); }
}

package com.company.scopery.modules.elicitation.session.application.response;

import java.util.List;
import java.util.Map;

public record ScopeTreeEntityResponse(
        String id,
        String title,
        String type,
        List<ScopeTreeEntityResponse> children,
        String pendingAction,
        Map<String, FieldChange> changes
) {
    public record FieldChange(Object before, Object after) {}

    public static ScopeTreeEntityResponse simple(String id, String title, String type) {
        return new ScopeTreeEntityResponse(id, title, type, List.of(), null, null);
    }

    public static ScopeTreeEntityResponse withAction(String id, String title, String type, String pendingAction) {
        return new ScopeTreeEntityResponse(id, title, type, List.of(), pendingAction, null);
    }
}

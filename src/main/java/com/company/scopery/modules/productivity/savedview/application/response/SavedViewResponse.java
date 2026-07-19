package com.company.scopery.modules.productivity.savedview.application.response;
import com.company.scopery.modules.productivity.savedview.domain.model.SavedView;
import java.util.UUID;
public record SavedViewResponse(UUID id, String targetType, String name, String status, String visibility) {
    public static SavedViewResponse from(SavedView v) { return new SavedViewResponse(v.id(), v.targetType(), v.name(), v.status(), v.visibility()); }
}

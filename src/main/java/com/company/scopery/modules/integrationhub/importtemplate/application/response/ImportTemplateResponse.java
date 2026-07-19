package com.company.scopery.modules.integrationhub.importtemplate.application.response;
import com.company.scopery.modules.integrationhub.importtemplate.domain.model.ImportTemplate;
import java.util.UUID;
public record ImportTemplateResponse(UUID id, UUID workspaceId, String templateCode, String name,
        String targetObjectType, String sourceFormat, boolean enabled) {
    public static ImportTemplateResponse from(ImportTemplate t) {
        return new ImportTemplateResponse(t.id(), t.workspaceId(), t.templateCode(), t.name(),
                t.targetObjectType(), t.sourceFormat(), t.enabled());
    }
}

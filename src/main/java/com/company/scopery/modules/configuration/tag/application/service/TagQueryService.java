package com.company.scopery.modules.configuration.tag.application.service;
import com.company.scopery.modules.configuration.shared.authorization.ConfigurationAuthorizationService;
import com.company.scopery.modules.configuration.shared.error.ConfigurationExceptions;
import com.company.scopery.modules.configuration.tag.application.response.TagDefinitionResponse;
import com.company.scopery.modules.configuration.tag.domain.model.TagDefinitionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class TagQueryService {
    private final TagDefinitionRepository tags; private final ConfigurationAuthorizationService authorization;
    public TagQueryService(TagDefinitionRepository tags, ConfigurationAuthorizationService authorization) { this.tags=tags; this.authorization=authorization; }
    @Transactional(readOnly=true)
    public List<TagDefinitionResponse> list(UUID workspaceId) {
        authorization.requireFieldView(workspaceId);
        return tags.findByWorkspaceId(workspaceId).stream().map(TagDefinitionResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public TagDefinitionResponse get(UUID workspaceId, UUID tagId) {
        authorization.requireFieldView(workspaceId);
        return TagDefinitionResponse.from(tags.findByIdAndWorkspaceId(tagId, workspaceId).orElseThrow(() -> ConfigurationExceptions.tagNotFound(tagId)));
    }
}

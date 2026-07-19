package com.company.scopery.modules.configuration.layout.application.service;
import com.company.scopery.modules.configuration.layout.application.response.LayoutDefinitionResponse;
import com.company.scopery.modules.configuration.layout.domain.model.LayoutDefinitionRepository;
import com.company.scopery.modules.configuration.shared.authorization.ConfigurationAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class LayoutQueryService {
    private final LayoutDefinitionRepository layouts; private final ConfigurationAuthorizationService authorization;
    public LayoutQueryService(LayoutDefinitionRepository layouts, ConfigurationAuthorizationService authorization) { this.layouts=layouts; this.authorization=authorization; }
    @Transactional(readOnly=true)
    public List<LayoutDefinitionResponse> list(UUID workspaceId) {
        authorization.requireFieldView(workspaceId);
        return layouts.findByWorkspaceId(workspaceId).stream().map(LayoutDefinitionResponse::from).toList();
    }
}

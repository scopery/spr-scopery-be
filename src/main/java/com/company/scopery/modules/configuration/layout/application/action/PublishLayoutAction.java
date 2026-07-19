package com.company.scopery.modules.configuration.layout.application.action;
import com.company.scopery.modules.configuration.layout.application.response.LayoutDefinitionResponse;
import com.company.scopery.modules.configuration.layout.domain.model.*;
import com.company.scopery.modules.configuration.shared.authorization.ConfigurationAuthorizationService;
import com.company.scopery.modules.configuration.shared.error.ConfigurationExceptions;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.configuration.layout.application.command.PublishLayoutCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class PublishLayoutAction {
    private final LayoutDefinitionRepository layouts; private final ConfigurationAuthorizationService authorization;
    public PublishLayoutAction(LayoutDefinitionRepository layouts, ConfigurationAuthorizationService authorization) {
        this.layouts=layouts; this.authorization=authorization;
    }
    @Transactional
    public LayoutDefinitionResponse execute(PublishLayoutCommand c) {
        authorization.requireFieldUpdate(c.workspaceId());
        var l = layouts.findByIdAndWorkspaceId(c.layoutId(), c.workspaceId()).orElseThrow(() -> ConfigurationExceptions.layoutNotFound(c.layoutId()));
        return LayoutDefinitionResponse.from(layouts.save(l.publish()));
    }
}

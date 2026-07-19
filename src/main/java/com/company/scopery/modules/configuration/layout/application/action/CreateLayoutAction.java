package com.company.scopery.modules.configuration.layout.application.action;
import com.company.scopery.modules.configuration.layout.application.response.LayoutDefinitionResponse;
import com.company.scopery.modules.configuration.layout.domain.model.*;
import com.company.scopery.modules.configuration.shared.activity.ConfigurationActivityLogger;
import com.company.scopery.modules.configuration.shared.authorization.ConfigurationAuthorizationService;
import com.company.scopery.modules.configuration.shared.constant.*;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.configuration.layout.application.command.CreateLayoutCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateLayoutAction {
    private final LayoutDefinitionRepository layouts; private final ConfigurationAuthorizationService authorization; private final ConfigurationActivityLogger activityLogger;
    public CreateLayoutAction(LayoutDefinitionRepository layouts, ConfigurationAuthorizationService authorization, ConfigurationActivityLogger activityLogger) {
        this.layouts=layouts; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public LayoutDefinitionResponse execute(CreateLayoutCommand c) {
        authorization.requireFieldCreate(c.workspaceId());
        var l = layouts.save(LayoutDefinition.create(c.workspaceId(), c.objectType(), c.layoutType(), c.name(), c.layoutJson()));
        activityLogger.logSuccess(ConfigurationEntityTypes.LAYOUT, l.id(), ConfigurationActivityActions.LAYOUT_CREATED, "Layout created");
        return LayoutDefinitionResponse.from(l);
    }
}

package com.company.scopery.modules.configuration.tag.application.action;
import com.company.scopery.modules.configuration.shared.activity.ConfigurationActivityLogger;
import com.company.scopery.modules.configuration.shared.authorization.ConfigurationAuthorizationService;
import com.company.scopery.modules.configuration.shared.constant.*;
import com.company.scopery.modules.configuration.shared.error.ConfigurationExceptions;
import com.company.scopery.modules.configuration.tag.application.response.TagDefinitionResponse;
import com.company.scopery.modules.configuration.tag.domain.model.*;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.configuration.tag.application.command.CreateTagCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateTagAction {
    private final TagDefinitionRepository tags; private final ConfigurationAuthorizationService authorization; private final ConfigurationActivityLogger activityLogger;
    public CreateTagAction(TagDefinitionRepository tags, ConfigurationAuthorizationService authorization, ConfigurationActivityLogger activityLogger) {
        this.tags=tags; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public TagDefinitionResponse execute(CreateTagCommand c) {
        authorization.requireFieldCreate(c.workspaceId());
        if (tags.existsByCode(c.workspaceId(), c.code())) throw ConfigurationExceptions.fieldKeyExists(c.code());
        var t = tags.save(TagDefinition.create(c.workspaceId(), c.code(), c.label(), c.color(), c.allowed()));
        activityLogger.logSuccess(ConfigurationEntityTypes.TAG, t.id(), ConfigurationActivityActions.TAG_CREATED, "Tag created");
        return TagDefinitionResponse.from(t);
    }
}

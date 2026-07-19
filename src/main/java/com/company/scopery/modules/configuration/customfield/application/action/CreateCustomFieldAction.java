package com.company.scopery.modules.configuration.customfield.application.action;
import com.company.scopery.modules.configuration.customfield.application.response.CustomFieldDefinitionResponse;
import com.company.scopery.modules.configuration.customfield.domain.enums.CustomFieldType;
import com.company.scopery.modules.configuration.customfield.domain.model.*;
import com.company.scopery.modules.configuration.shared.activity.ConfigurationActivityLogger;
import com.company.scopery.modules.configuration.shared.authorization.ConfigurationAuthorizationService;
import com.company.scopery.modules.configuration.shared.constant.*;
import com.company.scopery.modules.configuration.shared.error.ConfigurationExceptions;
import com.company.scopery.modules.configuration.shared.util.ConfigurationEnumParser;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.configuration.customfield.application.command.CreateCustomFieldCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateCustomFieldAction {
    private final CustomFieldDefinitionRepository fields; private final ConfigurationAuthorizationService authorization; private final ConfigurationActivityLogger activityLogger;
    public CreateCustomFieldAction(CustomFieldDefinitionRepository fields, ConfigurationAuthorizationService authorization, ConfigurationActivityLogger activityLogger) {
        this.fields=fields; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public CustomFieldDefinitionResponse execute(CreateCustomFieldCommand c) {
        authorization.requireFieldCreate(c.workspaceId());
        if (c.label() == null || c.label().isBlank()) throw ConfigurationExceptions.nameRequired();
        if (fields.existsByKey(c.workspaceId(), c.objectType(), c.fieldKey())) throw ConfigurationExceptions.fieldKeyExists(c.fieldKey());
        var type = ConfigurationEnumParser.parseRequired(CustomFieldType.class, c.fieldType(), "fieldType");
        var d = fields.save(CustomFieldDefinition.create(c.workspaceId(), c.objectType(), c.fieldKey(), c.label().trim(), type,
                Boolean.TRUE.equals(c.required()), Boolean.TRUE.equals(c.sensitive()), Boolean.TRUE.equals(c.clientVisible())));
        activityLogger.logSuccess(ConfigurationEntityTypes.CUSTOM_FIELD, d.id(), ConfigurationActivityActions.FIELD_CREATED, "Custom field created");
        return CustomFieldDefinitionResponse.from(d);
    }
}

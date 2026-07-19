package com.company.scopery.modules.configuration.fieldoption.application.action;
import com.company.scopery.modules.configuration.customfield.domain.model.CustomFieldDefinitionRepository;
import com.company.scopery.modules.configuration.fieldoption.application.response.CustomFieldOptionResponse;
import com.company.scopery.modules.configuration.fieldoption.domain.model.*;
import com.company.scopery.modules.configuration.shared.activity.ConfigurationActivityLogger;
import com.company.scopery.modules.configuration.shared.authorization.ConfigurationAuthorizationService;
import com.company.scopery.modules.configuration.shared.constant.*;
import com.company.scopery.modules.configuration.shared.error.ConfigurationExceptions;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.configuration.fieldoption.application.command.CreateFieldOptionCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateFieldOptionAction {
    private final CustomFieldDefinitionRepository fields; private final CustomFieldOptionRepository options;
    private final ConfigurationAuthorizationService authorization; private final ConfigurationActivityLogger activityLogger;
    public CreateFieldOptionAction(CustomFieldDefinitionRepository fields, CustomFieldOptionRepository options,
                                   ConfigurationAuthorizationService authorization, ConfigurationActivityLogger activityLogger) {
        this.fields=fields; this.options=options; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public CustomFieldOptionResponse execute(CreateFieldOptionCommand c) {
        authorization.requireFieldUpdate(c.workspaceId());
        fields.findByIdAndWorkspaceId(c.fieldId(), c.workspaceId()).orElseThrow(() -> ConfigurationExceptions.fieldNotFound(c.fieldId()));
        var o = options.save(CustomFieldOption.create(c.fieldId(), c.code(), c.label(), c.sortOrder() == null ? 0 : c.sortOrder()));
        activityLogger.logSuccess(ConfigurationEntityTypes.FIELD_OPTION, o.id(), ConfigurationActivityActions.OPTION_CREATED, "Option created");
        return CustomFieldOptionResponse.from(o);
    }
}

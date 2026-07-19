package com.company.scopery.modules.configuration.fieldoption.application.action;
import com.company.scopery.modules.configuration.customfield.domain.model.CustomFieldDefinitionRepository;
import com.company.scopery.modules.configuration.fieldoption.application.response.CustomFieldOptionResponse;
import com.company.scopery.modules.configuration.fieldoption.domain.model.*;
import com.company.scopery.modules.configuration.shared.authorization.ConfigurationAuthorizationService;
import com.company.scopery.modules.configuration.shared.error.ConfigurationExceptions;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.configuration.fieldoption.application.command.ArchiveFieldOptionCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class ArchiveFieldOptionAction {
    private final CustomFieldDefinitionRepository fields; private final CustomFieldOptionRepository options;
    private final ConfigurationAuthorizationService authorization;
    public ArchiveFieldOptionAction(CustomFieldDefinitionRepository fields, CustomFieldOptionRepository options, ConfigurationAuthorizationService authorization) {
        this.fields=fields; this.options=options; this.authorization=authorization;
    }
    @Transactional
    public CustomFieldOptionResponse execute(ArchiveFieldOptionCommand c) {
        authorization.requireFieldUpdate(c.workspaceId());
        fields.findByIdAndWorkspaceId(c.fieldId(), c.workspaceId()).orElseThrow(() -> ConfigurationExceptions.fieldNotFound(c.fieldId()));
        var o = options.findById(c.optionId()).orElseThrow(() -> ConfigurationExceptions.optionNotFound(c.optionId()));
        if (!o.customFieldDefinitionId().equals(c.fieldId())) throw ConfigurationExceptions.optionNotFound(c.optionId());
        return CustomFieldOptionResponse.from(options.save(o.archive()));
    }
}

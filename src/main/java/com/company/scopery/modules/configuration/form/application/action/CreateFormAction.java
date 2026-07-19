package com.company.scopery.modules.configuration.form.application.action;
import com.company.scopery.modules.configuration.form.application.response.CustomFormDefinitionResponse;
import com.company.scopery.modules.configuration.form.domain.model.*;
import com.company.scopery.modules.configuration.shared.activity.ConfigurationActivityLogger;
import com.company.scopery.modules.configuration.shared.authorization.ConfigurationAuthorizationService;
import com.company.scopery.modules.configuration.shared.constant.*;
import com.company.scopery.modules.configuration.shared.error.ConfigurationExceptions;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.configuration.form.application.command.CreateFormCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateFormAction {
    private final CustomFormDefinitionRepository forms; private final CustomFormVersionRepository versions;
    private final ConfigurationAuthorizationService authorization; private final ConfigurationActivityLogger activityLogger;
    public CreateFormAction(CustomFormDefinitionRepository forms, CustomFormVersionRepository versions,
                            ConfigurationAuthorizationService authorization, ConfigurationActivityLogger activityLogger) {
        this.forms=forms; this.versions=versions; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public CustomFormDefinitionResponse execute(CreateFormCommand c) {
        authorization.requireFormManage(c.workspaceId());
        if (forms.existsByCode(c.workspaceId(), c.formCode())) throw ConfigurationExceptions.fieldKeyExists(c.formCode());
        var form = forms.save(CustomFormDefinition.create(c.workspaceId(), c.projectId(), c.objectType(), c.formCode(), c.name(), c.formType() == null ? "CUSTOM" : c.formType()));
        var v = versions.save(CustomFormVersion.create(form.id(), c.workspaceId(), 1));
        form = forms.save(form.withCurrentVersion(v.id()));
        activityLogger.logSuccess(ConfigurationEntityTypes.FORM, form.id(), ConfigurationActivityActions.FORM_CREATED, "Form created");
        return CustomFormDefinitionResponse.from(form);
    }
}

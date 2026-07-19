package com.company.scopery.modules.configuration.form.application.action;
import com.company.scopery.modules.configuration.form.application.response.CustomFormVersionResponse;
import com.company.scopery.modules.configuration.form.domain.model.*;
import com.company.scopery.modules.configuration.shared.activity.ConfigurationActivityLogger;
import com.company.scopery.modules.configuration.shared.authorization.ConfigurationAuthorizationService;
import com.company.scopery.modules.configuration.shared.constant.*;
import com.company.scopery.modules.configuration.shared.error.ConfigurationExceptions;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.configuration.form.application.command.CreateFormVersionCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateFormVersionAction {
    private final CustomFormDefinitionRepository forms; private final CustomFormVersionRepository versions;
    private final ConfigurationAuthorizationService authorization; private final ConfigurationActivityLogger activityLogger;
    public CreateFormVersionAction(CustomFormDefinitionRepository forms, CustomFormVersionRepository versions,
                                   ConfigurationAuthorizationService authorization, ConfigurationActivityLogger activityLogger) {
        this.forms=forms; this.versions=versions; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public CustomFormVersionResponse execute(CreateFormVersionCommand c) {
        authorization.requireFormManage(c.workspaceId());
        forms.findByIdAndWorkspaceId(c.formId(), c.workspaceId()).orElseThrow(() -> ConfigurationExceptions.formNotFound(c.formId()));
        var v = versions.save(CustomFormVersion.create(c.formId(), c.workspaceId(), versions.nextVersionNumber(c.formId())));
        activityLogger.logSuccess(ConfigurationEntityTypes.FORM_VERSION, v.id(), ConfigurationActivityActions.FORM_VERSION_CREATED, "Form version created");
        return CustomFormVersionResponse.from(v);
    }
}

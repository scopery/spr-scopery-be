package com.company.scopery.modules.configuration.form.application.action;
import com.company.scopery.modules.configuration.form.application.response.CustomFormVersionResponse;
import com.company.scopery.modules.configuration.form.domain.model.*;
import com.company.scopery.modules.configuration.shared.activity.ConfigurationActivityLogger;
import com.company.scopery.modules.configuration.shared.authorization.ConfigurationAuthorizationService;
import com.company.scopery.modules.configuration.shared.constant.*;
import com.company.scopery.modules.configuration.shared.error.ConfigurationExceptions;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.configuration.form.application.command.PublishFormVersionCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class PublishFormVersionAction {
    private final CustomFormDefinitionRepository forms; private final CustomFormVersionRepository versions;
    private final ConfigurationAuthorizationService authorization; private final ConfigurationActivityLogger activityLogger;
    public PublishFormVersionAction(CustomFormDefinitionRepository forms, CustomFormVersionRepository versions,
                                    ConfigurationAuthorizationService authorization, ConfigurationActivityLogger activityLogger) {
        this.forms=forms; this.versions=versions; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public CustomFormVersionResponse execute(PublishFormVersionCommand c) {
        authorization.requireFormManage(c.workspaceId());
        var form = forms.findByIdAndWorkspaceId(c.formId(), c.workspaceId()).orElseThrow(() -> ConfigurationExceptions.formNotFound(c.formId()));
        var v = versions.findById(c.versionId()).orElseThrow(() -> ConfigurationExceptions.formVersionNotFound(c.versionId()));
        if (!v.formDefinitionId().equals(c.formId())) throw ConfigurationExceptions.formVersionNotFound(c.versionId());
        if (!v.isDraft()) throw ConfigurationExceptions.formVersionImmutable();
        var published = versions.save(v.publish());
        forms.save(form.withCurrentVersion(published.id()));
        activityLogger.logSuccess(ConfigurationEntityTypes.FORM_VERSION, published.id(), ConfigurationActivityActions.FORM_VERSION_PUBLISHED, "Form version published");
        return CustomFormVersionResponse.from(published);
    }
}

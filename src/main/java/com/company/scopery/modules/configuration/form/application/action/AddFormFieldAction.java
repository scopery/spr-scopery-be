package com.company.scopery.modules.configuration.form.application.action;
import com.company.scopery.modules.configuration.form.application.response.CustomFormFieldResponse;
import com.company.scopery.modules.configuration.form.domain.model.*;
import com.company.scopery.modules.configuration.shared.authorization.ConfigurationAuthorizationService;
import com.company.scopery.modules.configuration.shared.error.ConfigurationExceptions;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.configuration.form.application.command.AddFormFieldCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class AddFormFieldAction {
    private final CustomFormDefinitionRepository forms; private final CustomFormVersionRepository versions;
    private final CustomFormFieldRepository fields; private final ConfigurationAuthorizationService authorization;
    public AddFormFieldAction(CustomFormDefinitionRepository forms, CustomFormVersionRepository versions,
                              CustomFormFieldRepository fields, ConfigurationAuthorizationService authorization) {
        this.forms=forms; this.versions=versions; this.fields=fields; this.authorization=authorization;
    }
    @Transactional
    public CustomFormFieldResponse execute(AddFormFieldCommand c) {
        authorization.requireFormManage(c.workspaceId());
        forms.findByIdAndWorkspaceId(c.formId(), c.workspaceId()).orElseThrow(() -> ConfigurationExceptions.formNotFound(c.formId()));
        var v = versions.findById(c.versionId()).orElseThrow(() -> ConfigurationExceptions.formVersionNotFound(c.versionId()));
        if (!v.isDraft()) throw ConfigurationExceptions.formVersionImmutable();
        return CustomFormFieldResponse.from(fields.save(CustomFormField.create(c.versionId(), c.sectionId(), c.source(), c.customFieldId(), c.coreKey(),
                Boolean.TRUE.equals(c.required()), Boolean.TRUE.equals(c.readonly()), c.sortOrder() == null ? 0 : c.sortOrder())));
    }
}

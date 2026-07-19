package com.company.scopery.modules.configuration.form.application.action;
import com.company.scopery.modules.configuration.form.application.response.CustomFormSectionResponse;
import com.company.scopery.modules.configuration.form.domain.model.*;
import com.company.scopery.modules.configuration.shared.authorization.ConfigurationAuthorizationService;
import com.company.scopery.modules.configuration.shared.error.ConfigurationExceptions;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.configuration.form.application.command.AddFormSectionCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class AddFormSectionAction {
    private final CustomFormDefinitionRepository forms; private final CustomFormVersionRepository versions;
    private final CustomFormSectionRepository sections; private final ConfigurationAuthorizationService authorization;
    public AddFormSectionAction(CustomFormDefinitionRepository forms, CustomFormVersionRepository versions,
                                CustomFormSectionRepository sections, ConfigurationAuthorizationService authorization) {
        this.forms=forms; this.versions=versions; this.sections=sections; this.authorization=authorization;
    }
    @Transactional
    public CustomFormSectionResponse execute(AddFormSectionCommand c) {
        authorization.requireFormManage(c.workspaceId());
        forms.findByIdAndWorkspaceId(c.formId(), c.workspaceId()).orElseThrow(() -> ConfigurationExceptions.formNotFound(c.formId()));
        var v = versions.findById(c.versionId()).orElseThrow(() -> ConfigurationExceptions.formVersionNotFound(c.versionId()));
        if (!v.isDraft()) throw ConfigurationExceptions.formVersionImmutable();
        return CustomFormSectionResponse.from(sections.save(CustomFormSection.create(c.versionId(), c.title(), c.sortOrder() == null ? 0 : c.sortOrder())));
    }
}

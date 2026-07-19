package com.company.scopery.modules.configuration.form.application.service;
import com.company.scopery.modules.configuration.form.application.response.*;
import com.company.scopery.modules.configuration.form.domain.model.*;
import com.company.scopery.modules.configuration.shared.authorization.ConfigurationAuthorizationService;
import com.company.scopery.modules.configuration.shared.error.ConfigurationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class FormQueryService {
    private final CustomFormDefinitionRepository forms; private final CustomFormVersionRepository versions;
    private final CustomFormSectionRepository sections; private final CustomFormFieldRepository fields;
    private final FormSubmissionRepository submissions; private final ConfigurationAuthorizationService authorization;
    public FormQueryService(CustomFormDefinitionRepository forms, CustomFormVersionRepository versions, CustomFormSectionRepository sections,
                            CustomFormFieldRepository fields, FormSubmissionRepository submissions, ConfigurationAuthorizationService authorization) {
        this.forms=forms; this.versions=versions; this.sections=sections; this.fields=fields; this.submissions=submissions; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<CustomFormDefinitionResponse> listForms(UUID workspaceId) {
        authorization.requireFormView(workspaceId);
        return forms.findByWorkspaceId(workspaceId).stream().map(CustomFormDefinitionResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public CustomFormDefinitionResponse getForm(UUID workspaceId, UUID formId) {
        authorization.requireFormView(workspaceId);
        return CustomFormDefinitionResponse.from(forms.findByIdAndWorkspaceId(formId, workspaceId).orElseThrow(() -> ConfigurationExceptions.formNotFound(formId)));
    }
    @Transactional(readOnly=true)
    public List<CustomFormVersionResponse> listVersions(UUID workspaceId, UUID formId) {
        authorization.requireFormView(workspaceId);
        forms.findByIdAndWorkspaceId(formId, workspaceId).orElseThrow(() -> ConfigurationExceptions.formNotFound(formId));
        return versions.findByFormId(formId).stream().map(CustomFormVersionResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public List<CustomFormSectionResponse> listSections(UUID workspaceId, UUID formId, UUID versionId) {
        authorization.requireFormView(workspaceId);
        forms.findByIdAndWorkspaceId(formId, workspaceId).orElseThrow(() -> ConfigurationExceptions.formNotFound(formId));
        return sections.findByVersionId(versionId).stream().map(CustomFormSectionResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public List<CustomFormFieldResponse> listFields(UUID workspaceId, UUID formId, UUID versionId) {
        authorization.requireFormView(workspaceId);
        forms.findByIdAndWorkspaceId(formId, workspaceId).orElseThrow(() -> ConfigurationExceptions.formNotFound(formId));
        return fields.findByVersionId(versionId).stream().map(CustomFormFieldResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public List<FormSubmissionResponse> listSubmissions(UUID workspaceId) {
        authorization.requireFormView(workspaceId);
        return submissions.findByWorkspaceId(workspaceId).stream().map(FormSubmissionResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public FormSubmissionResponse getSubmission(UUID workspaceId, UUID submissionId) {
        authorization.requireFormView(workspaceId);
        return FormSubmissionResponse.from(submissions.findByIdAndWorkspaceId(submissionId, workspaceId).orElseThrow(() -> ConfigurationExceptions.submissionNotFound(submissionId)));
    }
}

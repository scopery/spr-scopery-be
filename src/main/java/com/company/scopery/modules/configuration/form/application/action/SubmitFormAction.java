package com.company.scopery.modules.configuration.form.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.configuration.form.application.response.FormSubmissionResponse;
import com.company.scopery.modules.configuration.form.domain.model.*;
import com.company.scopery.modules.configuration.shared.activity.ConfigurationActivityLogger;
import com.company.scopery.modules.configuration.shared.authorization.ConfigurationAuthorizationService;
import com.company.scopery.modules.configuration.shared.constant.*;
import com.company.scopery.modules.configuration.shared.error.ConfigurationExceptions;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.configuration.form.application.command.SubmitFormCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class SubmitFormAction {
    private final CustomFormDefinitionRepository forms; private final CustomFormVersionRepository versions;
    private final CustomFormFieldRepository formFields; private final FormSubmissionRepository submissions;
    private final ConfigurationAuthorizationService authorization; private final CurrentUserAuthorizationService currentUser;
    private final ConfigurationActivityLogger activityLogger;
    public SubmitFormAction(CustomFormDefinitionRepository forms, CustomFormVersionRepository versions, CustomFormFieldRepository formFields,
                            FormSubmissionRepository submissions, ConfigurationAuthorizationService authorization,
                            CurrentUserAuthorizationService currentUser, ConfigurationActivityLogger activityLogger) {
        this.forms=forms; this.versions=versions; this.formFields=formFields; this.submissions=submissions;
        this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public FormSubmissionResponse execute(SubmitFormCommand c) {
        authorization.requireFormView(c.workspaceId());
        forms.findByIdAndWorkspaceId(c.formId(), c.workspaceId()).orElseThrow(() -> ConfigurationExceptions.formNotFound(c.formId()));
        var v = versions.findById(c.versionId()).orElseThrow(() -> ConfigurationExceptions.formVersionNotFound(c.versionId()));
        if (!v.formDefinitionId().equals(c.formId())) throw ConfigurationExceptions.formVersionNotFound(c.versionId());
        if (!v.isPublished()) throw ConfigurationExceptions.formNotPublished();
        // Required-on-form enforcement: if payload blank for required marker fields, reject — lightweight check on missing payload.
        if (c.payloadJson() == null || c.payloadJson().isBlank() || "{}".equals(c.payloadJson().trim())) {
            boolean hasRequired = formFields.findByVersionId(c.versionId()).stream().anyMatch(CustomFormField::requiredOnForm);
            if (hasRequired) {
                var rejected = submissions.save(FormSubmission.rejected(c.workspaceId(), c.formId(), c.versionId(), currentUser.resolveCurrentUser().id(), c.payloadJson() == null ? "{}" : c.payloadJson(), "[\"Required fields missing\"]"));
                return FormSubmissionResponse.from(rejected);
            }
        }
        var accepted = submissions.save(FormSubmission.accepted(c.workspaceId(), c.projectId(), c.formId(), c.versionId(), c.objectType(), c.targetId(), currentUser.resolveCurrentUser().id(), c.payloadJson()));
        activityLogger.logSuccess(ConfigurationEntityTypes.FORM_SUBMISSION, accepted.id(), ConfigurationActivityActions.FORM_SUBMITTED, "Form submitted");
        return FormSubmissionResponse.from(accepted);
    }
}

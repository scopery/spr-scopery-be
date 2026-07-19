package com.company.scopery.modules.configuration.form.application.action;
import com.company.scopery.modules.clientportal.shared.security.CurrentPortalAccountService;
import com.company.scopery.modules.clientportal.shared.security.PortalGrantEnforcementService;
import com.company.scopery.modules.configuration.form.application.response.FormSubmissionResponse;
import com.company.scopery.modules.configuration.form.domain.model.*;
import com.company.scopery.modules.configuration.shared.activity.ConfigurationActivityLogger;
import com.company.scopery.modules.configuration.shared.constant.*;
import com.company.scopery.modules.configuration.shared.error.ConfigurationExceptions;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.configuration.form.application.command.SubmitPortalFormCommand;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Component
public class SubmitPortalFormAction {
    private final ProjectRepository projects;
    private final CustomFormDefinitionRepository forms;
    private final CustomFormVersionRepository versions;
    private final CustomFormFieldRepository formFields;
    private final FormSubmissionRepository submissions;
    private final PortalGrantEnforcementService grantEnforcement;
    private final CurrentPortalAccountService currentPortalAccount;
    private final ConfigurationActivityLogger activityLogger;

    public SubmitPortalFormAction(ProjectRepository projects, CustomFormDefinitionRepository forms,
                                  CustomFormVersionRepository versions, CustomFormFieldRepository formFields,
                                  FormSubmissionRepository submissions, PortalGrantEnforcementService grantEnforcement,
                                  CurrentPortalAccountService currentPortalAccount, ConfigurationActivityLogger activityLogger) {
        this.projects = projects; this.forms = forms; this.versions = versions; this.formFields = formFields;
        this.submissions = submissions; this.grantEnforcement = grantEnforcement;
        this.currentPortalAccount = currentPortalAccount; this.activityLogger = activityLogger;
    }

    @Transactional
    public FormSubmissionResponse execute(SubmitPortalFormCommand c) {
        grantEnforcement.requireActiveGrant(c.projectId());
        var project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        var form = forms.findByIdAndWorkspaceId(c.formId(), project.workspaceId())
                .orElseThrow(() -> ConfigurationExceptions.formNotFound(c.formId()));
        if (form.projectId() != null && !form.projectId().equals(c.projectId())) {
            throw ConfigurationExceptions.formNotFound(c.formId());
        }
        var v = versions.findById(c.versionId()).orElseThrow(() -> ConfigurationExceptions.formVersionNotFound(c.versionId()));
        if (!v.formDefinitionId().equals(c.formId())) throw ConfigurationExceptions.formVersionNotFound(c.versionId());
        if (!v.isPublished()) throw ConfigurationExceptions.formNotPublished();
        UUID portalAccountId = currentPortalAccount.requireCurrentPortalAccountId();
        if (c.payloadJson() == null || c.payloadJson().isBlank() || "{}".equals(c.payloadJson().trim())) {
            boolean hasRequired = formFields.findByVersionId(c.versionId()).stream().anyMatch(CustomFormField::requiredOnForm);
            if (hasRequired) {
                return FormSubmissionResponse.from(submissions.save(
                        FormSubmission.rejectedPortal(project.workspaceId(), c.formId(), c.versionId(), portalAccountId,
                                c.payloadJson() == null ? "{}" : c.payloadJson(), "[\"Required fields missing\"]")));
            }
        }
        var accepted = submissions.save(FormSubmission.acceptedPortal(
                project.workspaceId(), c.projectId(), c.formId(), c.versionId(), c.objectType(), c.targetId(), portalAccountId, c.payloadJson()));
        activityLogger.logSuccess(ConfigurationEntityTypes.FORM_SUBMISSION, accepted.id(),
                ConfigurationActivityActions.FORM_SUBMITTED, "Portal form submitted");
        return FormSubmissionResponse.from(accepted);
    }
}

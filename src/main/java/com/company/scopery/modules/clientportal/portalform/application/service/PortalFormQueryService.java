package com.company.scopery.modules.clientportal.portalform.application.service;
import com.company.scopery.modules.clientportal.shared.security.PortalGrantEnforcementService;
import com.company.scopery.modules.configuration.form.application.response.CustomFormDefinitionResponse;
import com.company.scopery.modules.configuration.form.application.response.CustomFormVersionResponse;
import com.company.scopery.modules.configuration.form.domain.model.CustomFormDefinitionRepository;
import com.company.scopery.modules.configuration.form.domain.model.CustomFormVersionRepository;
import com.company.scopery.modules.configuration.shared.error.ConfigurationExceptions;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.Locale; import java.util.UUID;

@Service
public class PortalFormQueryService {
    private final ProjectRepository projects;
    private final CustomFormDefinitionRepository forms;
    private final CustomFormVersionRepository versions;
    private final PortalGrantEnforcementService grantEnforcement;

    public PortalFormQueryService(ProjectRepository projects, CustomFormDefinitionRepository forms,
                                  CustomFormVersionRepository versions, PortalGrantEnforcementService grantEnforcement) {
        this.projects = projects; this.forms = forms; this.versions = versions; this.grantEnforcement = grantEnforcement;
    }

    @Transactional(readOnly = true)
    public List<CustomFormDefinitionResponse> listPublished(UUID projectId) {
        grantEnforcement.requireActiveGrant(projectId);
        var project = projects.findById(projectId).orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        return forms.findByWorkspaceId(project.workspaceId()).stream()
                .filter(f -> f.projectId() == null || projectId.equals(f.projectId()))
                .filter(f -> isClientFacing(f.formType()))
                .filter(f -> f.currentVersionId() != null)
                .map(CustomFormDefinitionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CustomFormVersionResponse getPublishedVersion(UUID projectId, UUID formId) {
        grantEnforcement.requireActiveGrant(projectId);
        var project = projects.findById(projectId).orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        var form = forms.findByIdAndWorkspaceId(formId, project.workspaceId())
                .orElseThrow(() -> ConfigurationExceptions.formNotFound(formId));
        if (form.projectId() != null && !form.projectId().equals(projectId)) throw ConfigurationExceptions.formNotFound(formId);
        if (!isClientFacing(form.formType())) throw ConfigurationExceptions.formNotFound(formId);
        if (form.currentVersionId() == null) throw ConfigurationExceptions.formNotPublished();
        var v = versions.findById(form.currentVersionId()).orElseThrow(() -> ConfigurationExceptions.formVersionNotFound(form.currentVersionId()));
        if (!v.isPublished()) throw ConfigurationExceptions.formNotPublished();
        return CustomFormVersionResponse.from(v);
    }

    private static boolean isClientFacing(String formType) {
        if (formType == null) return false;
        String t = formType.toUpperCase(Locale.ROOT);
        return t.contains("CLIENT") || t.contains("PORTAL") || t.contains("EXTERNAL");
    }
}

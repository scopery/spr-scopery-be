package com.company.scopery.modules.project.templatephase.application.service;

import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.TemplateAccessSupport;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplateRepository;
import com.company.scopery.modules.project.templatephase.application.response.ProjectTemplatePhaseResponse;
import com.company.scopery.modules.project.templatephase.domain.model.ProjectTemplatePhase;
import com.company.scopery.modules.project.templatephase.domain.model.ProjectTemplatePhaseRepository;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersion;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectTemplatePhaseQueryService {

    private final ProjectTemplateRepository templateRepository;
    private final ProjectTemplateVersionRepository versionRepository;
    private final ProjectTemplatePhaseRepository phaseRepository;
    private final TemplateAccessSupport authorizationSupport;

    public ProjectTemplatePhaseQueryService(ProjectTemplateRepository templateRepository,
                                            ProjectTemplateVersionRepository versionRepository,
                                            ProjectTemplatePhaseRepository phaseRepository,
                                            TemplateAccessSupport authorizationSupport) {
        this.templateRepository = templateRepository;
        this.versionRepository = versionRepository;
        this.phaseRepository = phaseRepository;
        this.authorizationSupport = authorizationSupport;
    }

    @Transactional(readOnly = true)
    public List<ProjectTemplatePhaseResponse> listPhases(UUID templateId, UUID versionId) {
        authorize(templateId, versionId);
        return phaseRepository.findByTemplateVersionId(versionId).stream()
                .map(ProjectTemplatePhaseResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectTemplatePhaseResponse getPhase(UUID templateId, UUID versionId, UUID phaseId) {
        authorize(templateId, versionId);
        ProjectTemplatePhase phase = phaseRepository.findById(phaseId)
                .orElseThrow(() -> ProjectExceptions.projectTemplatePhaseNotFound(phaseId));
        if (!phase.templateVersionId().equals(versionId)) {
            throw ProjectExceptions.projectTemplatePhasePathMismatch(phaseId, versionId);
        }
        return ProjectTemplatePhaseResponse.from(phase);
    }

    private void authorize(UUID templateId, UUID versionId) {
        ProjectTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> ProjectExceptions.projectTemplateNotFound(templateId));
        authorizationSupport.requireView(template);
        ProjectTemplateVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> ProjectExceptions.projectTemplateVersionNotFound(versionId));
        if (!version.projectTemplateId().equals(templateId)) {
            throw ProjectExceptions.projectTemplateVersionNotFound(versionId);
        }
    }
}

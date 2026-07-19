package com.company.scopery.modules.project.templatewbs.application.service;

import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.TemplateAccessSupport;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplateRepository;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersion;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersionRepository;
import com.company.scopery.modules.project.templatewbs.application.response.ProjectTemplateWbsNodeResponse;
import com.company.scopery.modules.project.templatewbs.domain.model.ProjectTemplateWbsNode;
import com.company.scopery.modules.project.templatewbs.domain.model.ProjectTemplateWbsNodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectTemplateWbsNodeQueryService {

    private final ProjectTemplateRepository templateRepository;
    private final ProjectTemplateVersionRepository versionRepository;
    private final ProjectTemplateWbsNodeRepository wbsRepository;
    private final TemplateAccessSupport authorizationSupport;

    public ProjectTemplateWbsNodeQueryService(ProjectTemplateRepository templateRepository,
                                              ProjectTemplateVersionRepository versionRepository,
                                              ProjectTemplateWbsNodeRepository wbsRepository,
                                              TemplateAccessSupport authorizationSupport) {
        this.templateRepository = templateRepository;
        this.versionRepository = versionRepository;
        this.wbsRepository = wbsRepository;
        this.authorizationSupport = authorizationSupport;
    }

    @Transactional(readOnly = true)
    public List<ProjectTemplateWbsNodeResponse> listNodes(UUID templateId, UUID versionId) {
        authorize(templateId, versionId);
        return wbsRepository.findByTemplateVersionId(versionId).stream()
                .map(ProjectTemplateWbsNodeResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectTemplateWbsNodeResponse getNode(UUID templateId, UUID versionId, UUID nodeId) {
        authorize(templateId, versionId);
        ProjectTemplateWbsNode node = wbsRepository.findById(nodeId)
                .orElseThrow(() -> ProjectExceptions.projectTemplateWbsNodeNotFound(nodeId));
        if (!node.templateVersionId().equals(versionId)) {
            throw ProjectExceptions.projectTemplateWbsNodePathMismatch(nodeId, versionId);
        }
        return ProjectTemplateWbsNodeResponse.from(node);
    }

    @Transactional(readOnly = true)
    public List<ProjectTemplateWbsNodeResponse> getTree(UUID templateId, UUID versionId) {
        authorize(templateId, versionId);
        return wbsRepository.findByTemplateVersionId(versionId).stream()
                .sorted(Comparator.comparingInt(ProjectTemplateWbsNode::depth)
                        .thenComparingInt(ProjectTemplateWbsNode::orderIndex))
                .map(ProjectTemplateWbsNodeResponse::from)
                .toList();
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

package com.company.scopery.modules.traceability.tracelink.application.action;

import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.quality.testcase.domain.model.TestCaseRepository;
import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItemRepository;
import com.company.scopery.modules.traceability.requirement.domain.model.RequirementRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.shared.util.TraceabilityEnumParser;
import com.company.scopery.modules.traceability.tracelink.application.command.CreateTraceLinkCommand;
import com.company.scopery.modules.traceability.tracelink.application.response.TraceLinkResponse;
import com.company.scopery.modules.traceability.tracelink.domain.enums.TraceLinkType;
import com.company.scopery.modules.traceability.tracelink.domain.model.TraceLink;
import com.company.scopery.modules.traceability.tracelink.domain.model.TraceLinkRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateTraceLinkAction {

    private final ProjectRepository projects;
    private final TraceLinkRepository repo;
    private final RequirementRepository requirements;
    private final TestCaseRepository testCases;
    private final FunctionalItemRepository functionalItems;
    private final TraceabilityAuthorizationService authorization;

    public CreateTraceLinkAction(ProjectRepository projects, TraceLinkRepository repo,
                                 RequirementRepository requirements,
                                 TestCaseRepository testCases,
                                 FunctionalItemRepository functionalItems,
                                 TraceabilityAuthorizationService authorization) {
        this.projects = projects;
        this.repo = repo;
        this.requirements = requirements;
        this.testCases = testCases;
        this.functionalItems = functionalItems;
        this.authorization = authorization;
    }

    @Transactional
    public TraceLinkResponse execute(CreateTraceLinkCommand c) {
        authorization.requireCreate(c.projectId());
        var project = projects.findById(c.projectId())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED)
            throw TraceabilityExceptions.projectArchived(c.projectId());

        TraceLinkType linkType = TraceabilityEnumParser.parseRequired(TraceLinkType.class, c.linkType(), "linkType");
        String sourceType = c.sourceType().trim();
        String targetType = c.targetType().trim();

        String[] sourceMeta = resolveDisplayMeta(sourceType, c.sourceId(), c.projectId());
        String[] targetMeta = resolveDisplayMeta(targetType, c.targetId(), c.projectId());

        return TraceLinkResponse.from(repo.save(TraceLink.create(
                project.id(), sourceType, c.sourceId(), targetType, c.targetId(), linkType,
                sourceMeta[0], sourceMeta[1], targetMeta[0], targetMeta[1])));
    }

    String[] resolveDisplayMeta(String entityType, java.util.UUID id, java.util.UUID projectId) {
        if (id == null) return new String[]{null, null};
        return switch (entityType.toUpperCase()) {
            case "REQUIREMENT" -> requirements.findByIdAndProjectId(id, projectId)
                    .map(r -> new String[]{r.code(), r.title()})
                    .orElse(new String[]{null, null});
            case "TEST_CASE" -> testCases.findByIdAndProjectId(id, projectId)
                    .map(tc -> new String[]{tc.code(), tc.title()})
                    .orElse(new String[]{null, null});
            case "FUNCTIONAL_ITEM" -> functionalItems.findByIdAndProjectId(id, projectId)
                    .map(fi -> new String[]{fi.code(), fi.title()})
                    .orElse(new String[]{null, null});
            default -> new String[]{null, null};
        };
    }
}

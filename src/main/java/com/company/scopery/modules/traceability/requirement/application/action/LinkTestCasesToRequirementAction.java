package com.company.scopery.modules.traceability.requirement.application.action;

import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.quality.testcase.domain.model.TestCaseRepository;
import com.company.scopery.modules.traceability.requirement.domain.model.RequirementRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.tracelink.application.response.BatchTraceLinkResponse;
import com.company.scopery.modules.traceability.tracelink.application.response.TraceLinkResponse;
import com.company.scopery.modules.traceability.tracelink.domain.enums.TraceLinkType;
import com.company.scopery.modules.traceability.tracelink.domain.model.TraceLink;
import com.company.scopery.modules.traceability.tracelink.domain.model.TraceLinkRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class LinkTestCasesToRequirementAction {

    private final ProjectRepository projects;
    private final RequirementRepository requirements;
    private final TestCaseRepository testCases;
    private final TraceLinkRepository links;
    private final TraceabilityAuthorizationService authorization;

    public LinkTestCasesToRequirementAction(ProjectRepository projects,
                                            RequirementRepository requirements,
                                            TestCaseRepository testCases,
                                            TraceLinkRepository links,
                                            TraceabilityAuthorizationService authorization) {
        this.projects = projects;
        this.requirements = requirements;
        this.testCases = testCases;
        this.links = links;
        this.authorization = authorization;
    }

    @Transactional
    public BatchTraceLinkResponse execute(UUID projectId, UUID requirementId, List<UUID> testCaseIds) {
        authorization.requireCreate(projectId);
        var project = projects.findById(projectId)
                .orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        if (project.status() == ProjectStatus.ARCHIVED)
            throw TraceabilityExceptions.projectArchived(projectId);

        var req = requirements.findByIdAndProjectId(requirementId, projectId)
                .orElseThrow(() -> TraceabilityExceptions.requirementNotFound(requirementId));

        List<TraceLinkResponse> created = new ArrayList<>();
        List<BatchTraceLinkResponse.SkippedLink> skipped = new ArrayList<>();
        List<BatchTraceLinkResponse.FailedLink> failed = new ArrayList<>();

        for (UUID tcId : testCaseIds) {
            try {
                if (links.existsActiveLink(projectId, "REQUIREMENT", requirementId,
                        "TEST_CASE", tcId, TraceLinkType.TESTED_BY.name())) {
                    skipped.add(new BatchTraceLinkResponse.SkippedLink(
                            requirementId, tcId, "TESTED_BY", "ALREADY_EXISTS"));
                    continue;
                }
                var tc = testCases.findByIdAndProjectId(tcId, projectId).orElse(null);
                String tcCode = tc != null ? tc.code() : null;
                String tcTitle = tc != null ? tc.title() : null;

                TraceLink link = TraceLink.create(projectId, "REQUIREMENT", requirementId,
                        "TEST_CASE", tcId, TraceLinkType.TESTED_BY,
                        req.code(), req.title(), tcCode, tcTitle);
                created.add(TraceLinkResponse.from(links.save(link)));

            } catch (Exception ex) {
                failed.add(new BatchTraceLinkResponse.FailedLink(
                        requirementId, tcId, "TESTED_BY", ex.getMessage()));
            }
        }

        return new BatchTraceLinkResponse(created, skipped, failed);
    }
}

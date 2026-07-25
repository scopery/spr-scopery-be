package com.company.scopery.modules.traceability.tracelink.application.action;

import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.shared.util.TraceabilityEnumParser;
import com.company.scopery.modules.traceability.tracelink.application.response.BatchTraceLinkResponse;
import com.company.scopery.modules.traceability.tracelink.application.response.TraceLinkResponse;
import com.company.scopery.modules.traceability.tracelink.domain.enums.TraceLinkType;
import com.company.scopery.modules.traceability.tracelink.domain.model.TraceLink;
import com.company.scopery.modules.traceability.tracelink.domain.model.TraceLinkRepository;
import com.company.scopery.modules.traceability.tracelink.http.request.BatchCreateTraceLinkRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class BatchCreateTraceLinkAction {

    private final ProjectRepository projects;
    private final TraceLinkRepository repo;
    private final CreateTraceLinkAction delegate;
    private final TraceabilityAuthorizationService authorization;

    public BatchCreateTraceLinkAction(ProjectRepository projects, TraceLinkRepository repo,
                                      CreateTraceLinkAction delegate,
                                      TraceabilityAuthorizationService authorization) {
        this.projects = projects;
        this.repo = repo;
        this.delegate = delegate;
        this.authorization = authorization;
    }

    @Transactional
    public BatchTraceLinkResponse execute(UUID projectId, List<BatchCreateTraceLinkRequest.LinkDto> links) {
        authorization.requireCreate(projectId);
        var project = projects.findById(projectId)
                .orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        if (project.status() == ProjectStatus.ARCHIVED)
            throw TraceabilityExceptions.projectArchived(projectId);

        List<TraceLinkResponse> created = new ArrayList<>();
        List<BatchTraceLinkResponse.SkippedLink> skipped = new ArrayList<>();
        List<BatchTraceLinkResponse.FailedLink> failed = new ArrayList<>();

        for (var dto : links) {
            try {
                TraceLinkType linkType = TraceabilityEnumParser.parseRequired(
                        TraceLinkType.class, dto.linkType(), "linkType");
                String sourceType = dto.sourceType().trim();
                String targetType = dto.targetType().trim();

                if (repo.existsActiveLink(projectId, sourceType, dto.sourceId(),
                        targetType, dto.targetId(), linkType.name())) {
                    skipped.add(new BatchTraceLinkResponse.SkippedLink(
                            dto.sourceId(), dto.targetId(), dto.linkType(), "ALREADY_EXISTS"));
                    continue;
                }

                String[] sourceMeta = delegate.resolveDisplayMeta(sourceType, dto.sourceId(), projectId);
                String[] targetMeta = delegate.resolveDisplayMeta(targetType, dto.targetId(), projectId);

                TraceLink link = TraceLink.create(projectId, sourceType, dto.sourceId(),
                        targetType, dto.targetId(), linkType,
                        sourceMeta[0], sourceMeta[1], targetMeta[0], targetMeta[1]);
                created.add(TraceLinkResponse.from(repo.save(link)));

            } catch (Exception ex) {
                failed.add(new BatchTraceLinkResponse.FailedLink(
                        dto.sourceId(), dto.targetId(), dto.linkType(), ex.getMessage()));
            }
        }

        return new BatchTraceLinkResponse(created, skipped, failed);
    }
}

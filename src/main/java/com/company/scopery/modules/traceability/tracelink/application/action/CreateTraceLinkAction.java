package com.company.scopery.modules.traceability.tracelink.application.action;

import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
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
    private final TraceabilityAuthorizationService authorization;

    public CreateTraceLinkAction(ProjectRepository projects, TraceLinkRepository repo, TraceabilityAuthorizationService authorization) {
        this.projects = projects; this.repo = repo; this.authorization = authorization;
    }

    @Transactional
    public TraceLinkResponse execute(CreateTraceLinkCommand c) {
        authorization.requireCreate(c.projectId());
        var project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) throw TraceabilityExceptions.projectArchived(c.projectId());
        TraceLinkType linkType = TraceabilityEnumParser.parseRequired(TraceLinkType.class, c.linkType(), "linkType");
        return TraceLinkResponse.from(repo.save(TraceLink.create(project.id(), c.sourceType().trim(), c.sourceId(),
                c.targetType().trim(), c.targetId(), linkType)));
    }
}

package com.company.scopery.modules.traceability.screenspecdoc.application.service;

import com.company.scopery.modules.traceability.screen.application.service.RegistryScreenQueryService;
import com.company.scopery.modules.traceability.screenspecdoc.application.response.RegistryScreenSpecDocResponse;
import com.company.scopery.modules.traceability.screenspecdoc.application.response.RegistryScreenSpecDocWithScreensResponse;
import com.company.scopery.modules.traceability.screenspecdoc.application.response.SpecDocFullSpecResponse;
import com.company.scopery.modules.traceability.screenspecdoc.domain.model.RegistryScreenSpecDocumentRepository;
import com.company.scopery.modules.traceability.screenspecdoc.domain.model.SpecDocScreen;
import com.company.scopery.modules.traceability.screenspecdoc.domain.model.SpecDocScreenRepository;
import com.company.scopery.modules.traceability.specdocrevision.domain.model.RegistrySpecDocRevision;
import com.company.scopery.modules.traceability.specdocrevision.domain.model.RegistrySpecDocRevisionRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RegistryScreenSpecDocQueryService {

    private final RegistryScreenSpecDocumentRepository docRepo;
    private final SpecDocScreenRepository screenRepo;
    private final RegistrySpecDocRevisionRepository revisionRepo;
    private final RegistryScreenQueryService screenQueryService;
    private final TraceabilityAuthorizationService authorization;

    public RegistryScreenSpecDocQueryService(RegistryScreenSpecDocumentRepository docRepo,
                                             SpecDocScreenRepository screenRepo,
                                             RegistrySpecDocRevisionRepository revisionRepo,
                                             RegistryScreenQueryService screenQueryService,
                                             TraceabilityAuthorizationService authorization) {
        this.docRepo = docRepo;
        this.screenRepo = screenRepo;
        this.revisionRepo = revisionRepo;
        this.screenQueryService = screenQueryService;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<RegistryScreenSpecDocResponse> list(UUID workspaceId, UUID projectId) {
        authorization.requireWorkspaceView(workspaceId);
        var docs = projectId != null
                ? docRepo.findByProjectIdOrderByCreatedAtDesc(projectId)
                : docRepo.findByWorkspaceIdOrderByCreatedAtDesc(workspaceId);
        return docs.stream().map(RegistryScreenSpecDocResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public RegistryScreenSpecDocWithScreensResponse get(UUID workspaceId, UUID projectId, UUID documentId) {
        authorization.requireWorkspaceView(workspaceId);
        var doc = docRepo.findByIdAndWorkspaceId(documentId, workspaceId)
                .orElseThrow(() -> TraceabilityExceptions.screenSpecDocNotFound(documentId));
        var screens = screenRepo.findByDocumentId(documentId);
        return RegistryScreenSpecDocWithScreensResponse.from(doc, screens);
    }

    @Transactional(readOnly = true)
    public SpecDocFullSpecResponse getFullSpec(UUID workspaceId, UUID documentId) {
        authorization.requireWorkspaceView(workspaceId);
        var doc = docRepo.findByIdAndWorkspaceId(documentId, workspaceId)
                .orElseThrow(() -> TraceabilityExceptions.screenSpecDocNotFound(documentId));

        List<RegistrySpecDocRevision> revisions = revisionRepo.findByDocumentIdOrderByDisplayOrderAsc(documentId);
        List<SpecDocScreen> docScreens = screenRepo.findByDocumentId(documentId);

        List<SpecDocFullSpecResponse.RevisionEntry> revisionEntries = revisions.stream()
                .map(r -> new SpecDocFullSpecResponse.RevisionEntry(
                        r.id(), r.revisionNo(), r.targetSheetName(), r.details(),
                        r.personInCharge(), r.color(), r.changedAt(), r.displayOrder()))
                .toList();

        List<SpecDocFullSpecResponse.ScreenSpecEntry> screenEntries = docScreens.stream()
                .sorted(java.util.Comparator.comparingInt(SpecDocScreen::displayOrder))
                .map(s -> new SpecDocFullSpecResponse.ScreenSpecEntry(
                        s.screenId(),
                        s.displayOrder(),
                        s.note(),
                        screenQueryService.getFullSpec(workspaceId, s.screenId())))
                .toList();

        return new SpecDocFullSpecResponse(
                doc.id(),
                doc.projectId(),
                doc.workspaceId(),
                doc.documentCode(),
                doc.documentName(),
                doc.projectName(),
                doc.systemName(),
                doc.phaseName(),
                doc.language(),
                doc.overview(),
                doc.figmaUrl(),
                doc.status().name(),
                doc.createdAt(),
                revisionEntries,
                screenEntries);
    }
}

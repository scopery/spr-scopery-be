package com.company.scopery.modules.traceability.functionalitem.application.action;

import com.company.scopery.modules.traceability.functionalitem.application.command.PreviewFunctionalItemsImportCommand;
import com.company.scopery.modules.traceability.functionalitem.application.response.FunctionalItemImportCandidate;
import com.company.scopery.modules.traceability.functionalitem.application.response.FunctionalItemImportConflict;
import com.company.scopery.modules.traceability.functionalitem.application.response.FunctionalItemImportDiff;
import com.company.scopery.modules.traceability.functionalitem.application.response.FunctionalItemImportPreviewResponse;
import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItem;
import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItemRepository;
import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItemTitleMatch;
import com.company.scopery.modules.traceability.functionalitem.http.request.ImportFunctionalItemEntry;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class PreviewFunctionalItemsImportAction {

    private static final double CONFIDENT_THRESHOLD = 0.7;
    private static final double CANDIDATE_THRESHOLD = 0.4;
    private static final int MAX_CANDIDATES = 3;

    private final FunctionalItemRepository repo;
    private final TraceabilityAuthorizationService authorization;

    public PreviewFunctionalItemsImportAction(FunctionalItemRepository repo,
                                              TraceabilityAuthorizationService authorization) {
        this.repo = repo;
        this.authorization = authorization;
    }

    public FunctionalItemImportPreviewResponse execute(PreviewFunctionalItemsImportCommand c) {
        authorization.requireView(c.projectId());

        List<FunctionalItem> existing = repo.findByProjectId(c.projectId());
        Map<String, FunctionalItem> byCode = new LinkedHashMap<>();
        for (FunctionalItem item : existing) {
            if (item.code() != null && !item.code().isBlank()) {
                byCode.put(item.code().trim().toLowerCase(), item);
            }
        }

        List<ImportFunctionalItemEntry> toCreate = new ArrayList<>();
        List<FunctionalItemImportDiff> toUpdate = new ArrayList<>();
        List<FunctionalItemImportConflict> conflicts = new ArrayList<>();

        for (ImportFunctionalItemEntry entry : c.items()) {
            if (entry.title() == null || entry.title().isBlank()) {
                throw TraceabilityExceptions.importInvalidFunctionalItem("Title must not be blank");
            }

            if (entry.code() != null && !entry.code().isBlank()) {
                FunctionalItem match = byCode.get(entry.code().trim().toLowerCase());
                if (match != null) {
                    toUpdate.add(buildDiff(match, entry));
                } else {
                    toCreate.add(entry);
                }
            } else {
                List<FunctionalItemTitleMatch> candidates = repo.findSimilarByTitle(
                        c.projectId(), entry.title(), CANDIDATE_THRESHOLD, MAX_CANDIDATES);

                if (candidates.isEmpty()) {
                    toCreate.add(entry);
                } else if (candidates.get(0).similarity() >= CONFIDENT_THRESHOLD) {
                    FunctionalItemTitleMatch best = candidates.get(0);
                    FunctionalItem match = existing.stream()
                            .filter(it -> it.id().equals(best.id()))
                            .findFirst()
                            .orElse(null);
                    if (match != null) {
                        toUpdate.add(buildDiff(match, entry));
                    } else {
                        toCreate.add(entry);
                    }
                } else {
                    List<FunctionalItemImportCandidate> candidateList = candidates.stream()
                            .map(m -> new FunctionalItemImportCandidate(m.id(), m.code(), m.title(), m.similarity()))
                            .toList();
                    conflicts.add(new FunctionalItemImportConflict(entry, candidateList));
                }
            }
        }

        return new FunctionalItemImportPreviewResponse(toCreate, toUpdate, conflicts);
    }

    private FunctionalItemImportDiff buildDiff(FunctionalItem existing, ImportFunctionalItemEntry incoming) {
        Map<String, Object[]> changes = new LinkedHashMap<>();
        if (incoming.title() != null && !incoming.title().equals(existing.title())) {
            changes.put("title", new Object[]{existing.title(), incoming.title()});
        }
        if (incoming.description() != null && !incoming.description().equals(existing.description())) {
            changes.put("description", new Object[]{existing.description(), incoming.description()});
        }
        if (incoming.priority() != null && existing.priority() != null
                && !incoming.priority().equals(existing.priority().name())) {
            changes.put("priority", new Object[]{existing.priority().name(), incoming.priority()});
        }
        if (incoming.type() != null && existing.type() != null
                && !incoming.type().equals(existing.type().name())) {
            changes.put("type", new Object[]{existing.type().name(), incoming.type()});
        }
        if (incoming.moduleId() != null && !incoming.moduleId().equals(existing.moduleId())) {
            changes.put("moduleId", new Object[]{existing.moduleId(), incoming.moduleId()});
        }
        return new FunctionalItemImportDiff(existing.id(), existing.code(), existing.title(), incoming, changes);
    }
}

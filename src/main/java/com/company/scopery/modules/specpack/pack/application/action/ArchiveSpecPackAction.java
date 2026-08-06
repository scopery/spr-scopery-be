package com.company.scopery.modules.specpack.pack.application.action;

import com.company.scopery.modules.specpack.pack.application.response.SpecPackResponse;
import com.company.scopery.modules.specpack.pack.domain.model.SpecPack;
import com.company.scopery.modules.specpack.pack.domain.model.SpecPackRepository;
import com.company.scopery.modules.specpack.shared.activity.SpecPackActivityLogger;
import com.company.scopery.modules.specpack.shared.constant.SpecPackActivityActions;
import com.company.scopery.modules.specpack.shared.constant.SpecPackEntityTypes;
import com.company.scopery.modules.specpack.shared.error.SpecPackExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ArchiveSpecPackAction {

    private final SpecPackRepository repository;
    private final SpecPackActivityLogger activityLogger;

    public ArchiveSpecPackAction(SpecPackRepository repository, SpecPackActivityLogger activityLogger) {
        this.repository = repository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public SpecPackResponse execute(UUID projectId, UUID packId) {
        SpecPack pack = findOrThrow(packId, projectId);
        pack.archive();
        SpecPack saved = repository.save(pack);

        activityLogger.logSuccess(
                SpecPackEntityTypes.SPEC_PACK,
                saved.id(),
                SpecPackActivityActions.SPEC_PACK_ARCHIVED,
                "Spec pack archived: " + saved.name()
        );

        return SpecPackResponse.from(saved);
    }

    private SpecPack findOrThrow(UUID packId, UUID projectId) {
        SpecPack pack = repository.findById(packId)
                .orElseThrow(() -> SpecPackExceptions.specPackNotFound(packId));
        if (!pack.projectId().equals(projectId)) {
            throw SpecPackExceptions.specPackProjectMismatch(packId, projectId);
        }
        return pack;
    }
}

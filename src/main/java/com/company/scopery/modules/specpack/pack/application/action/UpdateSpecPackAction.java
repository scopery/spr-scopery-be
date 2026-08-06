package com.company.scopery.modules.specpack.pack.application.action;

import com.company.scopery.modules.specpack.pack.application.command.UpdateSpecPackCommand;
import com.company.scopery.modules.specpack.pack.application.response.SpecPackResponse;
import com.company.scopery.modules.specpack.pack.domain.model.SpecPack;
import com.company.scopery.modules.specpack.pack.domain.model.SpecPackRepository;
import com.company.scopery.modules.specpack.shared.activity.SpecPackActivityLogger;
import com.company.scopery.modules.specpack.shared.constant.SpecPackActivityActions;
import com.company.scopery.modules.specpack.shared.constant.SpecPackEntityTypes;
import com.company.scopery.modules.specpack.shared.error.SpecPackExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateSpecPackAction {

    private final SpecPackRepository repository;
    private final SpecPackActivityLogger activityLogger;

    public UpdateSpecPackAction(SpecPackRepository repository, SpecPackActivityLogger activityLogger) {
        this.repository = repository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public SpecPackResponse execute(UpdateSpecPackCommand command) {
        SpecPack pack = findOrThrow(command.packId(), command.projectId());
        if (pack.status().name().equals("ARCHIVED")) {
            throw SpecPackExceptions.specPackArchived(command.packId());
        }

        pack.update(command.name(), command.description());
        SpecPack saved = repository.save(pack);

        activityLogger.logSuccess(
                SpecPackEntityTypes.SPEC_PACK,
                saved.id(),
                SpecPackActivityActions.SPEC_PACK_UPDATED,
                "Spec pack updated: " + saved.name()
        );

        return SpecPackResponse.from(saved);
    }

    private SpecPack findOrThrow(java.util.UUID packId, java.util.UUID projectId) {
        SpecPack pack = repository.findById(packId)
                .orElseThrow(() -> SpecPackExceptions.specPackNotFound(packId));
        if (!pack.projectId().equals(projectId)) {
            throw SpecPackExceptions.specPackProjectMismatch(packId, projectId);
        }
        return pack;
    }
}

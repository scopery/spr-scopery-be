package com.company.scopery.modules.specpack.version.application.action;

import com.company.scopery.modules.specpack.block.domain.model.SpecPackBlock;
import com.company.scopery.modules.specpack.block.domain.model.SpecPackBlockRepository;
import com.company.scopery.modules.specpack.pack.domain.model.SpecPack;
import com.company.scopery.modules.specpack.pack.domain.model.SpecPackRepository;
import com.company.scopery.modules.specpack.shared.activity.SpecPackActivityLogger;
import com.company.scopery.modules.specpack.shared.constant.SpecPackActivityActions;
import com.company.scopery.modules.specpack.shared.constant.SpecPackEntityTypes;
import com.company.scopery.modules.specpack.shared.error.SpecPackExceptions;
import com.company.scopery.modules.specpack.version.application.command.CreatePackVersionCommand;
import com.company.scopery.modules.specpack.version.application.response.SpecPackVersionResponse;
import com.company.scopery.modules.specpack.version.domain.model.SpecPackVersion;
import com.company.scopery.modules.specpack.version.domain.model.SpecPackVersionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class CreatePackVersionAction {

    private final SpecPackRepository packRepository;
    private final SpecPackBlockRepository blockRepository;
    private final SpecPackVersionRepository versionRepository;
    private final SpecPackActivityLogger activityLogger;

    public CreatePackVersionAction(SpecPackRepository packRepository,
                                   SpecPackBlockRepository blockRepository,
                                   SpecPackVersionRepository versionRepository,
                                   SpecPackActivityLogger activityLogger) {
        this.packRepository = packRepository;
        this.blockRepository = blockRepository;
        this.versionRepository = versionRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public SpecPackVersionResponse execute(CreatePackVersionCommand command) {
        SpecPack pack = packRepository.findById(command.specPackId())
                .orElseThrow(() -> SpecPackExceptions.specPackNotFound(command.specPackId()));
        if (!pack.projectId().equals(command.projectId())) {
            throw SpecPackExceptions.specPackNotFound(command.specPackId());
        }

        List<SpecPackBlock> liveBlocks = blockRepository.findAllByPackIdOrdered(command.specPackId());

        List<Map<String, Object>> snapshot = liveBlocks.stream()
                .map(CreatePackVersionAction::blockToSnapshotEntry)
                .toList();

        int nextVersionNumber = versionRepository.findMaxVersionNumberBySpecPackId(command.specPackId())
                .map(max -> max + 1)
                .orElse(1);

        SpecPackVersion version = SpecPackVersion.create(
                command.specPackId(), nextVersionNumber, snapshot, null,
                liveBlocks.size(), 0, command.changeReason()
        );

        SpecPackVersion saved = versionRepository.save(version);

        pack.setCurrentVersionId(saved.id());
        packRepository.save(pack);

        activityLogger.logSuccess(SpecPackEntityTypes.SPEC_PACK_VERSION, saved.id(),
                SpecPackActivityActions.PACK_VERSION_CREATED,
                "Version " + nextVersionNumber + " created for pack " + command.specPackId());

        return SpecPackVersionResponse.from(saved);
    }

    private static Map<String, Object> blockToSnapshotEntry(SpecPackBlock block) {
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("id", block.id().toString());
        entry.put("blockKey", block.blockKey());
        entry.put("parentBlockId", block.parentBlockId() != null ? block.parentBlockId().toString() : null);
        entry.put("blockType", block.blockType().name());
        entry.put("title", block.title());
        entry.put("contentFormat", block.contentFormat().name());
        entry.put("contentJson", block.contentJson());
        entry.put("sourceRefsJson", block.sourceRefsJson());
        entry.put("displayOrder", block.displayOrder());
        entry.put("status", block.status().name());
        entry.put("revisionNumber", block.currentRevisionNumber());
        return entry;
    }
}

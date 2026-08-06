package com.company.scopery.modules.specpack.version.application.action;

import com.company.scopery.modules.specpack.block.domain.enums.BlockType;
import com.company.scopery.modules.specpack.block.domain.enums.ChangeSource;
import com.company.scopery.modules.specpack.block.domain.enums.ContentFormat;
import com.company.scopery.modules.specpack.block.domain.model.SpecPackBlock;
import com.company.scopery.modules.specpack.block.domain.model.SpecPackBlockRepository;
import com.company.scopery.modules.specpack.block.domain.model.SpecPackBlockRevision;
import com.company.scopery.modules.specpack.pack.domain.model.SpecPack;
import com.company.scopery.modules.specpack.pack.domain.model.SpecPackRepository;
import com.company.scopery.modules.specpack.shared.activity.SpecPackActivityLogger;
import com.company.scopery.modules.specpack.shared.constant.SpecPackActivityActions;
import com.company.scopery.modules.specpack.shared.constant.SpecPackEntityTypes;
import com.company.scopery.modules.specpack.shared.error.SpecPackExceptions;
import com.company.scopery.modules.specpack.version.application.command.RestorePackVersionCommand;
import com.company.scopery.modules.specpack.version.application.response.SpecPackVersionResponse;
import com.company.scopery.modules.specpack.version.domain.model.SpecPackVersion;
import com.company.scopery.modules.specpack.version.domain.model.SpecPackVersionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class RestorePackVersionAction {

    private final SpecPackRepository packRepository;
    private final SpecPackBlockRepository blockRepository;
    private final SpecPackVersionRepository versionRepository;
    private final SpecPackActivityLogger activityLogger;

    public RestorePackVersionAction(SpecPackRepository packRepository,
                                    SpecPackBlockRepository blockRepository,
                                    SpecPackVersionRepository versionRepository,
                                    SpecPackActivityLogger activityLogger) {
        this.packRepository = packRepository;
        this.blockRepository = blockRepository;
        this.versionRepository = versionRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public SpecPackVersionResponse execute(RestorePackVersionCommand command) {
        SpecPack pack = packRepository.findById(command.specPackId())
                .orElseThrow(() -> SpecPackExceptions.specPackNotFound(command.specPackId()));
        if (!pack.projectId().equals(command.projectId())) {
            throw SpecPackExceptions.specPackNotFound(command.specPackId());
        }

        SpecPackVersion version = versionRepository.findById(command.versionId())
                .orElseThrow(() -> SpecPackExceptions.versionNotFound(command.versionId()));
        if (!version.specPackId().equals(command.specPackId())) {
            throw SpecPackExceptions.versionNotFound(command.versionId());
        }

        List<SpecPackBlock> current = blockRepository.findAllByPackIdOrdered(command.specPackId());
        for (SpecPackBlock block : current) {
            block.softDelete();
            blockRepository.save(block);
        }

        for (Map<String, Object> entry : version.snapshotJson()) {
            SpecPackBlock restored = blockFromSnapshot(command.specPackId(), entry);
            SpecPackBlock saved = blockRepository.save(restored);

            SpecPackBlockRevision revision = SpecPackBlockRevision.create(
                    saved.id(), saved.currentRevisionNumber(), saved.title(),
                    saved.contentFormat(), saved.contentJson(), saved.sourceRefsJson(),
                    ChangeSource.RESTORE, "Restored from version " + version.versionNumber()
            );
            blockRepository.saveRevision(revision);
        }

        activityLogger.logSuccess(SpecPackEntityTypes.SPEC_PACK_VERSION, version.id(),
                SpecPackActivityActions.PACK_VERSION_RESTORED,
                "Pack " + command.specPackId() + " restored to version " + version.versionNumber());

        return SpecPackVersionResponse.from(version);
    }

    @SuppressWarnings("unchecked")
    private SpecPackBlock blockFromSnapshot(UUID specPackId, Map<String, Object> entry) {
        String blockKey = (String) entry.get("blockKey");
        String parentBlockIdStr = (String) entry.get("parentBlockId");
        UUID parentBlockId = parentBlockIdStr != null ? UUID.fromString(parentBlockIdStr) : null;
        BlockType blockType = BlockType.valueOf((String) entry.get("blockType"));
        String title = (String) entry.get("title");
        ContentFormat contentFormat = ContentFormat.valueOf((String) entry.get("contentFormat"));
        Map<String, Object> contentJson = (Map<String, Object>) entry.get("contentJson");
        List<Map<String, Object>> sourceRefsJson = (List<Map<String, Object>>) entry.get("sourceRefsJson");
        int displayOrder = entry.get("displayOrder") instanceof Number n ? n.intValue() : 0;

        return SpecPackBlock.create(specPackId, blockKey, parentBlockId,
                blockType, title, contentFormat, contentJson, sourceRefsJson, displayOrder);
    }
}

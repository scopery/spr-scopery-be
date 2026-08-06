package com.company.scopery.modules.specpack.blockimport.application.service;

import com.company.scopery.modules.specpack.block.domain.enums.BlockType;
import com.company.scopery.modules.specpack.block.domain.enums.ChangeSource;
import com.company.scopery.modules.specpack.block.domain.enums.ContentFormat;
import com.company.scopery.modules.specpack.block.domain.model.SpecPackBlock;
import com.company.scopery.modules.specpack.block.domain.model.SpecPackBlockRepository;
import com.company.scopery.modules.specpack.block.domain.model.SpecPackBlockRevision;
import com.company.scopery.modules.specpack.blockimport.domain.enums.BlockImportDecision;
import com.company.scopery.modules.specpack.blockimport.domain.model.BlockImportItem;
import com.company.scopery.modules.specpack.shared.util.SpecPackEnumParser;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class SpecPackMergeResolver {

    private final SpecPackBlockRepository blockRepository;

    public SpecPackMergeResolver(SpecPackBlockRepository blockRepository) {
        this.blockRepository = blockRepository;
    }

    public int apply(List<BlockImportItem> items, UUID specPackId, ChangeSource changeSource) {
        int applied = 0;
        for (BlockImportItem item : items) {
            if (!item.isValid()) continue;
            if (item.mergeDecision() == BlockImportDecision.SKIP) continue;

            BlockType blockType = SpecPackEnumParser.parseRequired(BlockType.class, item.blockType(), "blockType");
            ContentFormat contentFormat = SpecPackEnumParser.parseRequired(ContentFormat.class, item.contentFormat(), "contentFormat");

            UUID parentBlockId = resolveParentId(item.parentBlockKey(), specPackId);

            if (item.mergeDecision() == BlockImportDecision.REPLACE) {
                blockRepository.findByPackIdAndBlockKey(specPackId, item.blockKey()).ifPresent(existing -> {
                    existing.update(parentBlockId, item.title(), contentFormat,
                            item.contentJson(), item.sourceRefsJson(), existing.currentRevisionNumber());
                    SpecPackBlock saved = blockRepository.save(existing);
                    blockRepository.saveRevision(SpecPackBlockRevision.create(
                            saved.id(), saved.currentRevisionNumber(), saved.title(),
                            saved.contentFormat(), saved.contentJson(), saved.sourceRefsJson(),
                            changeSource, "Imported from " + changeSource.name().toLowerCase()
                    ));
                });
            } else {
                SpecPackBlock block = SpecPackBlock.create(specPackId, item.blockKey(), parentBlockId,
                        blockType, item.title(), contentFormat,
                        item.contentJson(), item.sourceRefsJson(), item.displayOrder());
                SpecPackBlock saved = blockRepository.save(block);
                blockRepository.saveRevision(SpecPackBlockRevision.create(
                        saved.id(), saved.currentRevisionNumber(), saved.title(),
                        saved.contentFormat(), saved.contentJson(), saved.sourceRefsJson(),
                        changeSource, "Imported from " + changeSource.name().toLowerCase()
                ));
            }
            applied++;
        }
        return applied;
    }

    private UUID resolveParentId(String parentBlockKey, UUID specPackId) {
        if (parentBlockKey == null || parentBlockKey.isBlank()) return null;
        return blockRepository.findByPackIdAndBlockKey(specPackId, parentBlockKey)
                .map(SpecPackBlock::id)
                .orElse(null);
    }
}

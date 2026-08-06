package com.company.scopery.modules.specpack.blockimport.application.service;

import com.company.scopery.modules.specpack.block.domain.model.SpecPackBlockRepository;
import com.company.scopery.modules.specpack.blockimport.domain.enums.BlockImportDecision;
import com.company.scopery.modules.specpack.blockimport.domain.enums.BlockMergeMode;
import com.company.scopery.modules.specpack.blockimport.domain.model.BlockImportItem;
import com.company.scopery.modules.specpack.blockimport.domain.model.ImportPreview;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class SpecPackImportPreviewBuilder {

    private final SpecPackBlockRepository blockRepository;

    public SpecPackImportPreviewBuilder(SpecPackBlockRepository blockRepository) {
        this.blockRepository = blockRepository;
    }

    public ImportPreview build(String schemaVersion, List<BlockImportItem> validated,
                               UUID specPackId, BlockMergeMode mergeMode) {
        List<BlockImportItem> withDecisions = validated.stream()
                .map(item -> item.isValid()
                        ? item.withDecision(resolveDecision(item, specPackId, mergeMode))
                        : item)
                .toList();

        long valid = withDecisions.stream().filter(BlockImportItem::isValid).count();
        long invalid = withDecisions.size() - valid;
        long creates = withDecisions.stream()
                .filter(i -> i.mergeDecision() == BlockImportDecision.CREATE).count();
        long replaces = withDecisions.stream()
                .filter(i -> i.mergeDecision() == BlockImportDecision.REPLACE).count();
        long skips = withDecisions.stream()
                .filter(i -> i.mergeDecision() == BlockImportDecision.SKIP).count();

        return new ImportPreview(schemaVersion, withDecisions.size(),
                (int) valid, (int) invalid,
                (int) creates, (int) replaces, (int) skips,
                withDecisions);
    }

    private BlockImportDecision resolveDecision(BlockImportItem item, UUID specPackId, BlockMergeMode mergeMode) {
        boolean exists = item.blockKey() != null
                && blockRepository.existsByPackIdAndBlockKey(specPackId, item.blockKey());

        return switch (mergeMode) {
            case CREATE_NEW_ONLY -> exists ? BlockImportDecision.SKIP : BlockImportDecision.CREATE;
            case MERGE_BY_BLOCK_KEY -> exists ? BlockImportDecision.REPLACE : BlockImportDecision.CREATE;
            case REPLACE_ALL -> BlockImportDecision.REPLACE;
        };
    }
}

package com.company.scopery.modules.specpack.block.application.service;

import com.company.scopery.modules.specpack.block.application.response.BlockResponse;
import com.company.scopery.modules.specpack.block.application.response.BlockRevisionResponse;
import com.company.scopery.modules.specpack.block.domain.model.SpecPackBlockRepository;
import com.company.scopery.modules.specpack.shared.error.SpecPackExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SpecPackBlockQueryService {

    private final SpecPackBlockRepository repository;

    public SpecPackBlockQueryService(SpecPackBlockRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public BlockResponse getById(UUID specPackId, UUID blockId) {
        return repository.findById(blockId)
                .filter(b -> b.specPackId().equals(specPackId) && !b.isDeleted())
                .map(BlockResponse::from)
                .orElseThrow(() -> SpecPackExceptions.blockNotFound(blockId));
    }

    @Transactional(readOnly = true)
    public List<BlockResponse> listByPack(UUID specPackId) {
        return repository.findAllByPackIdOrdered(specPackId).stream()
                .map(BlockResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BlockRevisionResponse> listRevisions(UUID specPackId, UUID blockId) {
        repository.findById(blockId)
                .filter(b -> b.specPackId().equals(specPackId))
                .orElseThrow(() -> SpecPackExceptions.blockNotFound(blockId));
        return repository.findRevisionsByBlockId(blockId).stream()
                .map(BlockRevisionResponse::from)
                .toList();
    }
}

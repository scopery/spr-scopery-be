package com.company.scopery.modules.traceability.structurerelation.application.service;

import com.company.scopery.modules.traceability.structurerelation.application.response.StructureRelationResponse;
import com.company.scopery.modules.traceability.structurerelation.domain.model.StructureRelationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class StructureRelationQueryService {

    private final StructureRelationRepository repo;

    public StructureRelationQueryService(StructureRelationRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<StructureRelationResponse> list(UUID applicationId, String nodeType, UUID nodeId) {
        if (nodeType != null && nodeId != null) {
            var from = repo.findByApplicationIdAndFromNode(applicationId, nodeType, nodeId);
            var to = repo.findByApplicationIdAndToNode(applicationId, nodeType, nodeId);
            return Stream.concat(from.stream(), to.stream())
                    .distinct()
                    .map(StructureRelationResponse::from)
                    .toList();
        }
        return repo.findByApplicationId(applicationId)
                .stream()
                .map(StructureRelationResponse::from)
                .toList();
    }
}

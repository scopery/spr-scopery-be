package com.company.scopery.modules.traceability.aimapping.run.domain.model;

import com.company.scopery.modules.traceability.aimapping.run.domain.enums.MappingRelationType;
import com.company.scopery.modules.traceability.aimapping.run.domain.enums.MappingRunStatus;

import java.util.Optional;
import java.util.UUID;

public interface MappingRunRepository {

    MappingRun save(MappingRun run);

    Optional<MappingRun> findById(UUID id);

    boolean existsByProjectIdAndRelationTypeAndStatus(UUID projectId,
                                                      MappingRelationType relationType,
                                                      MappingRunStatus status);
}

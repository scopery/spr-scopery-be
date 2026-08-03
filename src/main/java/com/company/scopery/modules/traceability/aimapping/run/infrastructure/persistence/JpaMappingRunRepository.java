package com.company.scopery.modules.traceability.aimapping.run.infrastructure.persistence;

import com.company.scopery.modules.traceability.aimapping.run.domain.enums.MappingRelationType;
import com.company.scopery.modules.traceability.aimapping.run.domain.enums.MappingRunStatus;
import com.company.scopery.modules.traceability.aimapping.run.domain.model.MappingRun;
import com.company.scopery.modules.traceability.aimapping.run.domain.model.MappingRunRepository;
import com.company.scopery.modules.traceability.aimapping.run.infrastructure.mapper.MappingRunPersistenceMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class JpaMappingRunRepository implements MappingRunRepository {

    private final SpringDataMappingRunJpaRepository springDataRepository;
    private final MappingRunPersistenceMapper mapper;

    public JpaMappingRunRepository(SpringDataMappingRunJpaRepository springDataRepository,
                                   MappingRunPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public MappingRun save(MappingRun run) {
        MappingRunJpaEntity entity = mapper.toJpaEntity(run);
        MappingRunJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<MappingRun> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByProjectIdAndRelationTypeAndStatus(UUID projectId, MappingRelationType relationType, MappingRunStatus status) {
        return springDataRepository.existsByProjectIdAndRelationTypeAndStatus(
                projectId, relationType.name(), status.name());
    }
}

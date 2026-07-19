package com.company.scopery.modules.resourcecapacity.actualeffort.infrastructure.persistence;
import com.company.scopery.modules.resourcecapacity.actualeffort.domain.model.*;
import com.company.scopery.modules.resourcecapacity.actualeffort.infrastructure.mapper.ActualEffortPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaActualEffortRecordRepository implements ActualEffortRecordRepository {
    private final SpringDataActualEffortJpaRepository spring; private final ActualEffortPersistenceMapper mapper;
    public JpaActualEffortRecordRepository(SpringDataActualEffortJpaRepository spring, ActualEffortPersistenceMapper mapper) {
        this.spring=spring; this.mapper=mapper;
    }
    @Override public ActualEffortRecord save(ActualEffortRecord r) { return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(r))); }
    @Override public Optional<ActualEffortRecord> findById(UUID id) { return spring.findById(id).map(mapper::toDomain); }
    @Override public List<ActualEffortRecord> findByProjectId(UUID projectId) {
        return spring.findByProjectId(projectId).stream().map(mapper::toDomain).toList();
    }
}

package com.company.scopery.modules.resourcecapacity.conflict.infrastructure.persistence;
import com.company.scopery.modules.resourcecapacity.conflict.domain.model.*;
import com.company.scopery.modules.resourcecapacity.conflict.infrastructure.mapper.AssignmentConflictPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaAssignmentConflictRepository implements AssignmentConflictRepository {
    private final SpringDataAssignmentConflictJpaRepository spring; private final AssignmentConflictPersistenceMapper mapper;
    public JpaAssignmentConflictRepository(SpringDataAssignmentConflictJpaRepository spring, AssignmentConflictPersistenceMapper mapper) {
        this.spring=spring; this.mapper=mapper;
    }
    @Override public AssignmentConflict save(AssignmentConflict c) { return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(c))); }
    @Override public Optional<AssignmentConflict> findById(UUID id) { return spring.findById(id).map(mapper::toDomain); }
    @Override public List<AssignmentConflict> findByProjectId(UUID projectId) {
        return spring.findByProjectId(projectId).stream().map(mapper::toDomain).toList();
    }
}

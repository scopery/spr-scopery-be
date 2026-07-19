package com.company.scopery.modules.configuration.tag.infrastructure.persistence;
import com.company.scopery.modules.configuration.tag.domain.model.*;
import com.company.scopery.modules.configuration.tag.infrastructure.mapper.TagAssignmentPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaTagAssignmentRepository implements TagAssignmentRepository {
    private final SpringDataTagAssignmentJpaRepository springData; private final TagAssignmentPersistenceMapper mapper;
    public JpaTagAssignmentRepository(SpringDataTagAssignmentJpaRepository springData, TagAssignmentPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public TagAssignment save(TagAssignment a) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(a))); }
    @Override public Optional<TagAssignment> findById(UUID id) { return springData.findById(id).map(mapper::toDomain); }
    @Override public List<TagAssignment> findActiveByWorkspace(UUID workspaceId) { return springData.findByWorkspaceIdAndArchivedAtIsNull(workspaceId).stream().map(mapper::toDomain).toList(); }
}

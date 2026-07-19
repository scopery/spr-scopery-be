package com.company.scopery.modules.servicesupport.assignment.infrastructure.persistence;

import com.company.scopery.modules.servicesupport.assignment.domain.model.SupportAssignment;
import com.company.scopery.modules.servicesupport.assignment.domain.model.SupportAssignmentRepository;
import com.company.scopery.modules.servicesupport.assignment.infrastructure.mapper.SupportAssignmentPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaSupportAssignmentRepository implements SupportAssignmentRepository {
    private final SpringDataSupportAssignmentJpaRepository spring;
    private final SupportAssignmentPersistenceMapper mapper;
    public JpaSupportAssignmentRepository(SpringDataSupportAssignmentJpaRepository spring, SupportAssignmentPersistenceMapper mapper) {
        this.spring = spring; this.mapper = mapper;
    }
    @Override public SupportAssignment save(SupportAssignment a) { return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(a))); }
    @Override public List<SupportAssignment> findBySupportCaseId(UUID caseId) {
        return spring.findBySupportCaseId(caseId).stream().map(mapper::toDomain).toList();
    }
    @Override public List<SupportAssignment> findByWorkspaceId(UUID workspaceId) {
        return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList();
    }
}

package com.company.scopery.modules.externalparty.authority.infrastructure.persistence;
import com.company.scopery.modules.externalparty.authority.domain.model.*;
import com.company.scopery.modules.externalparty.authority.infrastructure.mapper.ProjectApprovalAuthorityPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaProjectApprovalAuthorityRepository implements ProjectApprovalAuthorityRepository {
    private final SpringDataProjectApprovalAuthorityJpaRepository springData;
    private final ProjectApprovalAuthorityPersistenceMapper mapper;
    public JpaProjectApprovalAuthorityRepository(SpringDataProjectApprovalAuthorityJpaRepository springData, ProjectApprovalAuthorityPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public ProjectApprovalAuthority save(ProjectApprovalAuthority e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<ProjectApprovalAuthority> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
    @Override public List<ProjectApprovalAuthority> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }
}

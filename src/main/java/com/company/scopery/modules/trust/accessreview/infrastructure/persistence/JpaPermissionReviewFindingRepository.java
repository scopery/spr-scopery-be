package com.company.scopery.modules.trust.accessreview.infrastructure.persistence;
import com.company.scopery.modules.trust.accessreview.domain.model.*;
import com.company.scopery.modules.trust.accessreview.infrastructure.mapper.AccessReviewPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaPermissionReviewFindingRepository implements PermissionReviewFindingRepository {
    private final SpringDataPermissionReviewFindingJpaRepository spring; private final AccessReviewPersistenceMapper mapper;
    public JpaPermissionReviewFindingRepository(SpringDataPermissionReviewFindingJpaRepository spring, AccessReviewPersistenceMapper mapper){this.spring=spring;this.mapper=mapper;}
    @Override public PermissionReviewFinding save(PermissionReviewFinding f){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(f))); }
    @Override public Optional<PermissionReviewFinding> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public List<PermissionReviewFinding> findByWorkspaceId(UUID workspaceId){ return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList(); }
}

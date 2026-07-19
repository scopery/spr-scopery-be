package com.company.scopery.modules.clientportal.policy.infrastructure.persistence;
import com.company.scopery.modules.clientportal.policy.domain.model.ExternalPortalPermissionPolicy;
import com.company.scopery.modules.clientportal.policy.domain.model.ExternalPortalPermissionPolicyRepository;
import com.company.scopery.modules.clientportal.policy.infrastructure.mapper.ExternalPortalPermissionPolicyPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaExternalPortalPermissionPolicyRepository implements ExternalPortalPermissionPolicyRepository {
    private final SpringDataExternalPortalPermissionPolicyJpaRepository springData;
    private final ExternalPortalPermissionPolicyPersistenceMapper mapper;
    public JpaExternalPortalPermissionPolicyRepository(SpringDataExternalPortalPermissionPolicyJpaRepository springData,
                                                       ExternalPortalPermissionPolicyPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public ExternalPortalPermissionPolicy save(ExternalPortalPermissionPolicy e) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e)));
    }
    @Override public Optional<ExternalPortalPermissionPolicy> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }
    @Override public Optional<ExternalPortalPermissionPolicy> findByWorkspaceIdAndCode(UUID workspaceId, String code) {
        return springData.findByWorkspaceIdAndCode(workspaceId, code).map(mapper::toDomain);
    }
    @Override public List<ExternalPortalPermissionPolicy> findByWorkspaceId(UUID workspaceId) {
        return springData.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList();
    }
    @Override public boolean existsByWorkspaceIdAndCode(UUID workspaceId, String code) {
        return springData.existsByWorkspaceIdAndCode(workspaceId, code);
    }
}

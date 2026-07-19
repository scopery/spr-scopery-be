package com.company.scopery.modules.clientportal.auditlog.infrastructure.persistence;
import com.company.scopery.modules.clientportal.auditlog.domain.model.ExternalPortalAuditLog;
import com.company.scopery.modules.clientportal.auditlog.domain.model.ExternalPortalAuditLogRepository;
import com.company.scopery.modules.clientportal.auditlog.infrastructure.mapper.ExternalPortalAuditLogPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.UUID;
@Repository
public class JpaExternalPortalAuditLogRepository implements ExternalPortalAuditLogRepository {
    private final SpringDataExternalPortalAuditLogJpaRepository springData;
    private final ExternalPortalAuditLogPersistenceMapper mapper;
    public JpaExternalPortalAuditLogRepository(SpringDataExternalPortalAuditLogJpaRepository springData, ExternalPortalAuditLogPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public ExternalPortalAuditLog save(ExternalPortalAuditLog entity) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(entity)));
    }
    @Override public List<ExternalPortalAuditLog> findByProjectId(UUID projectId) {
        return springData.findByProjectId(projectId).stream().map(mapper::toDomain).toList();
    }
    @Override public List<ExternalPortalAuditLog> findByWorkspaceIdAndPortalAccountId(UUID workspaceId, UUID portalAccountId) {
        return springData.findByWorkspaceIdAndPortalAccountId(workspaceId, portalAccountId).stream().map(mapper::toDomain).toList();
    }
}

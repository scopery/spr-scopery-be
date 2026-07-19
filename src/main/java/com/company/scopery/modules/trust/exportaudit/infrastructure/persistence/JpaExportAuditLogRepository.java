package com.company.scopery.modules.trust.exportaudit.infrastructure.persistence;
import com.company.scopery.modules.trust.exportaudit.domain.model.*;
import com.company.scopery.modules.trust.exportaudit.infrastructure.mapper.ExportAuditLogPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaExportAuditLogRepository implements ExportAuditLogRepository {
    private final SpringDataExportAuditLogJpaRepository spring; private final ExportAuditLogPersistenceMapper mapper;
    public JpaExportAuditLogRepository(SpringDataExportAuditLogJpaRepository spring, ExportAuditLogPersistenceMapper mapper){this.spring=spring;this.mapper=mapper;}
    @Override public ExportAuditLog save(ExportAuditLog log){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(log))); }
    @Override public Optional<ExportAuditLog> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public List<ExportAuditLog> findByWorkspaceId(UUID workspaceId){ return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList(); }
}

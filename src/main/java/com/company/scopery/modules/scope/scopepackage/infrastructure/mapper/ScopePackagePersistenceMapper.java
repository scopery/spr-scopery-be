package com.company.scopery.modules.scope.scopepackage.infrastructure.mapper;
import com.company.scopery.modules.scope.scopepackage.domain.enums.ScopePackageStatus;
import com.company.scopery.modules.scope.scopepackage.domain.model.ScopePackage;
import com.company.scopery.modules.scope.scopepackage.infrastructure.persistence.ScopePackageJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ScopePackagePersistenceMapper {
    public ScopePackage toDomain(ScopePackageJpaEntity e) {
        return new ScopePackage(e.getId(), e.getProjectId(), e.getWorkspaceId(), e.getSourceQuoteVersionId(), e.getSourceBaselineId(),
                e.getCode(), e.getName(), e.getDescription(), ScopePackageStatus.valueOf(e.getStatus()), e.isCurrentFlag(),
                e.getApprovedAt(), e.getApprovedBy(), e.getArchivedAt(), e.getArchivedBy(), e.getTraceId(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public ScopePackageJpaEntity toJpaEntity(ScopePackage d) {
        ScopePackageJpaEntity e = new ScopePackageJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setWorkspaceId(d.workspaceId());
        e.setSourceQuoteVersionId(d.sourceQuoteVersionId()); e.setSourceBaselineId(d.sourceBaselineId());
        e.setCode(d.code()); e.setName(d.name()); e.setDescription(d.description()); e.setStatus(d.status().name());
        e.setCurrentFlag(d.currentFlag()); e.setApprovedAt(d.approvedAt()); e.setApprovedBy(d.approvedBy());
        e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy()); e.setTraceId(d.traceId()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}

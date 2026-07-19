package com.company.scopery.modules.quality.release.infrastructure.mapper;
import com.company.scopery.modules.quality.release.domain.enums.*; import com.company.scopery.modules.quality.release.domain.model.ReleasePackage;
import com.company.scopery.modules.quality.release.infrastructure.persistence.ReleasePackageJpaEntity; import org.springframework.stereotype.Component;
@Component
public class ReleasePackagePersistenceMapper {
    public ReleasePackage toDomain(ReleasePackageJpaEntity e) {
        return new ReleasePackage(e.getId(), e.getProjectId(), e.getWorkspaceId(), e.getCode(), e.getVersionLabel(), e.getName(),
                e.getDescription(), ReleaseType.valueOf(e.getReleaseType()), ReleasePackageStatus.valueOf(e.getStatus()),
                e.getPlannedReleaseDate(), e.getActualReleaseDate(),
                e.getReadinessStatus()==null?null:ReadinessStatus.valueOf(e.getReadinessStatus()),
                e.getReadinessSummaryJson(), e.getReleaseNotes(), e.getApprovedAt(), e.getApprovedBy(), e.getReleasedAt(), e.getReleasedBy(),
                e.getArchivedAt(), e.getArchivedBy(), e.getTraceId(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public ReleasePackageJpaEntity toJpaEntity(ReleasePackage d) {
        ReleasePackageJpaEntity e = new ReleasePackageJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setWorkspaceId(d.workspaceId()); e.setCode(d.code());
        e.setVersionLabel(d.versionLabel()); e.setName(d.name()); e.setDescription(d.description());
        e.setReleaseType(d.releaseType().name()); e.setStatus(d.status().name()); e.setPlannedReleaseDate(d.plannedReleaseDate());
        e.setActualReleaseDate(d.actualReleaseDate());
        e.setReadinessStatus(d.readinessStatus()==null?null:d.readinessStatus().name());
        e.setReadinessSummaryJson(d.readinessSummaryJson()); e.setReleaseNotes(d.releaseNotes());
        e.setApprovedAt(d.approvedAt()); e.setApprovedBy(d.approvedBy()); e.setReleasedAt(d.releasedAt()); e.setReleasedBy(d.releasedBy());
        e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy()); e.setTraceId(d.traceId()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}

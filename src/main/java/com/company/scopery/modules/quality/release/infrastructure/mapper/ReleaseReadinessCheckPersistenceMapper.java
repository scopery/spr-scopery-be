package com.company.scopery.modules.quality.release.infrastructure.mapper;
import com.company.scopery.modules.quality.release.domain.enums.ReadinessCheckStatus; import com.company.scopery.modules.quality.release.domain.model.ReleaseReadinessCheck;
import com.company.scopery.modules.quality.release.infrastructure.persistence.ReleaseReadinessCheckJpaEntity; import org.springframework.stereotype.Component;
@Component
public class ReleaseReadinessCheckPersistenceMapper {
    public ReleaseReadinessCheck toDomain(ReleaseReadinessCheckJpaEntity e) {
        return new ReleaseReadinessCheck(e.getId(), e.getProjectId(), e.getReleasePackageId(), e.getCheckCode(), e.getCheckName(),
                ReadinessCheckStatus.valueOf(e.getStatus()), e.getDetails(), e.isBlocking(), e.getOverrideReason(),
                e.getOverriddenAt(), e.getOverriddenBy(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public ReleaseReadinessCheckJpaEntity toJpaEntity(ReleaseReadinessCheck d) {
        ReleaseReadinessCheckJpaEntity e = new ReleaseReadinessCheckJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setReleasePackageId(d.releasePackageId());
        e.setCheckCode(d.checkCode()); e.setCheckName(d.checkName()); e.setStatus(d.status().name());
        e.setDetails(d.details()); e.setBlocking(d.blocking()); e.setOverrideReason(d.overrideReason());
        e.setOverriddenAt(d.overriddenAt()); e.setOverriddenBy(d.overriddenBy()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}

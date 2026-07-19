package com.company.scopery.modules.trust.privacy.infrastructure.mapper;
import com.company.scopery.modules.trust.privacy.domain.model.PrivacyExportPackage;
import com.company.scopery.modules.trust.privacy.infrastructure.persistence.PrivacyExportPackageJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class PrivacyExportPackagePersistenceMapper {
    public PrivacyExportPackageJpaEntity toJpa(PrivacyExportPackage d) {
        PrivacyExportPackageJpaEntity e = new PrivacyExportPackageJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setPrivacyRequestId(d.privacyRequestId());
        e.setDataSubjectIndexId(d.dataSubjectIndexId()); e.setStatus(d.status()); e.setPackageManifestJson(d.packageManifestJson());
        e.setFileReference(d.fileReference()); e.setExpiresAt(d.expiresAt()); e.setCompletedAt(d.completedAt());
        e.setVersion(d.version()); e.setCreatedAt(d.createdAt()); return e;
    }
    public PrivacyExportPackage toDomain(PrivacyExportPackageJpaEntity e) {
        return new PrivacyExportPackage(e.getId(), e.getWorkspaceId(), e.getPrivacyRequestId(), e.getDataSubjectIndexId(),
                e.getStatus(), e.getPackageManifestJson(), e.getFileReference(), e.getExpiresAt(), e.getCompletedAt(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt());
    }
}

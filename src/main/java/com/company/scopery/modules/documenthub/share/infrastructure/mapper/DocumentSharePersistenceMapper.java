package com.company.scopery.modules.documenthub.share.infrastructure.mapper;
import com.company.scopery.modules.documenthub.share.domain.enums.DocumentShareStatus;
import com.company.scopery.modules.documenthub.share.domain.model.DocumentShare;
import com.company.scopery.modules.documenthub.share.infrastructure.persistence.DocumentShareJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class DocumentSharePersistenceMapper {
    public DocumentShare toDomain(DocumentShareJpaEntity e) {
        return new DocumentShare(e.getId(), e.getDocumentId(), e.getProjectId(), e.getShareType(), e.getGranteeType(), e.getGranteeId(),
                e.getExpiresAt(), DocumentShareStatus.valueOf(e.getStatus()), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public DocumentShareJpaEntity toJpaEntity(DocumentShare d) {
        DocumentShareJpaEntity e = new DocumentShareJpaEntity();
        e.setId(d.id()); e.setDocumentId(d.documentId()); e.setProjectId(d.projectId());
        e.setShareType(d.shareType()); e.setGranteeType(d.granteeType()); e.setGranteeId(d.granteeId());
        e.setExpiresAt(d.expiresAt()); e.setStatus(d.status().name());
        if (d.createdAt()!=null) { e.setCreatedAt(d.createdAt()); e.setVersion(d.version()); }
        return e;
    }
}

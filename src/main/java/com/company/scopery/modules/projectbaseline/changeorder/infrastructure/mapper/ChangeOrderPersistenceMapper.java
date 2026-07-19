package com.company.scopery.modules.projectbaseline.changeorder.infrastructure.mapper;
import com.company.scopery.modules.projectbaseline.changeorder.domain.enums.ChangeOrderStatus;
import com.company.scopery.modules.projectbaseline.changeorder.domain.model.ChangeOrder;
import com.company.scopery.modules.projectbaseline.changeorder.infrastructure.persistence.ChangeOrderJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ChangeOrderPersistenceMapper {
    public ChangeOrder toDomain(ChangeOrderJpaEntity e) {
        return new ChangeOrder(e.getId(), e.getChangeRequestId(), e.getProjectId(), e.getWorkspaceId(),
                e.getCode(), e.getTitle(), e.getDescription(), ChangeOrderStatus.valueOf(e.getStatus()),
                e.getCommercialImpactJson(), e.getSourceQuoteVersionId(), e.getFutureContractId(),
                e.getApprovedAt(), e.getApprovedBy(), e.getRejectedAt(), e.getRejectedBy(), e.getRejectionReason(),
                e.getArchivedAt(), e.getArchivedBy(), e.getVersion()==null?0:e.getVersion(),
                e.getCreatedAt(), e.getUpdatedAt());
    }
    public ChangeOrderJpaEntity toJpaEntity(ChangeOrder d) {
        ChangeOrderJpaEntity e = new ChangeOrderJpaEntity();
        e.setId(d.id()); e.setChangeRequestId(d.changeRequestId()); e.setProjectId(d.projectId());
        e.setWorkspaceId(d.workspaceId()); e.setCode(d.code()); e.setTitle(d.title());
        e.setDescription(d.description()); e.setStatus(d.status().name());
        e.setCommercialImpactJson(d.commercialImpactJson()); e.setSourceQuoteVersionId(d.sourceQuoteVersionId());
        e.setFutureContractId(d.futureContractId()); e.setApprovedAt(d.approvedAt()); e.setApprovedBy(d.approvedBy());
        e.setRejectedAt(d.rejectedAt()); e.setRejectedBy(d.rejectedBy()); e.setRejectionReason(d.rejectionReason());
        e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}

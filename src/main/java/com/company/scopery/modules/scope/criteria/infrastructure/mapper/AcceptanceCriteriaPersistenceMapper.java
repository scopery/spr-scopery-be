package com.company.scopery.modules.scope.criteria.infrastructure.mapper;
import com.company.scopery.modules.scope.criteria.domain.enums.AcceptanceCriteriaStatus;
import com.company.scopery.modules.scope.criteria.domain.model.AcceptanceCriteria;
import com.company.scopery.modules.scope.criteria.infrastructure.persistence.AcceptanceCriteriaJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class AcceptanceCriteriaPersistenceMapper {
    public AcceptanceCriteria toDomain(AcceptanceCriteriaJpaEntity e) {
        return new AcceptanceCriteria(e.getId(), e.getDeliverableId(), e.getProjectId(), e.getType(), e.getTitle(), e.getDescription(),
                e.isMandatory(), AcceptanceCriteriaStatus.valueOf(e.getStatus()), e.getWaiveReason(), e.getWaivedBy(), e.getWaivedAt(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public AcceptanceCriteriaJpaEntity toJpaEntity(AcceptanceCriteria d) {
        AcceptanceCriteriaJpaEntity e = new AcceptanceCriteriaJpaEntity();
        e.setId(d.id()); e.setDeliverableId(d.deliverableId()); e.setProjectId(d.projectId()); e.setType(d.type());
        e.setTitle(d.title()); e.setDescription(d.description()); e.setMandatory(d.mandatory()); e.setStatus(d.status().name());
        e.setWaiveReason(d.waiveReason()); e.setWaivedBy(d.waivedBy()); e.setWaivedAt(d.waivedAt()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}

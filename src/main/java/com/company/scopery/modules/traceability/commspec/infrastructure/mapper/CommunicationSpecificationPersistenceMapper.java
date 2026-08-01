package com.company.scopery.modules.traceability.commspec.infrastructure.mapper;

import com.company.scopery.modules.traceability.commspec.domain.enums.CommunicationSpecStatus;
import com.company.scopery.modules.traceability.commspec.domain.model.CommunicationSpecification;
import com.company.scopery.modules.traceability.commspec.infrastructure.persistence.CommunicationSpecificationJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class CommunicationSpecificationPersistenceMapper {
    public CommunicationSpecification toDomain(CommunicationSpecificationJpaEntity e) {
        return new CommunicationSpecification(
                e.getId(), e.getApplicationId(), e.getWorkspaceId(), e.getCode(), e.getName(), e.getDescription(),
                CommunicationSpecStatus.valueOf(e.getStatus()),
                e.getTriggerName(), e.getTriggerKey(), e.getTriggerTiming(),
                e.getConditionJson(), e.getSuppressionConditionJson(), e.getDeliveryPolicyJson(),
                e.getInAppContractJson(), e.getEmailContractJson(), e.getRecipientsJson(), e.getOwnerId(),
                e.getVersion() == null ? 0 : e.getVersion(),
                e.getCreatedAt(), e.getUpdatedAt(), e.getArchivedAt());
    }

    public CommunicationSpecificationJpaEntity toJpaEntity(CommunicationSpecification d) {
        CommunicationSpecificationJpaEntity e = new CommunicationSpecificationJpaEntity();
        e.setId(d.id());
        e.setApplicationId(d.applicationId());
        e.setWorkspaceId(d.workspaceId());
        e.setCode(d.code());
        e.setName(d.name());
        e.setDescription(d.description());
        e.setStatus(d.status().name());
        e.setTriggerName(d.triggerName());
        e.setTriggerKey(d.triggerKey());
        e.setTriggerTiming(d.triggerTiming());
        e.setConditionJson(d.conditionJson());
        e.setSuppressionConditionJson(d.suppressionConditionJson());
        e.setDeliveryPolicyJson(d.deliveryPolicyJson());
        e.setInAppContractJson(d.inAppContractJson());
        e.setEmailContractJson(d.emailContractJson());
        e.setRecipientsJson(d.recipientsJson());
        e.setOwnerId(d.ownerId());
        e.setArchivedAt(d.archivedAt());
        // New entities: leave version/createdAt null so Persistable.isNew() → persist().
        if (d.createdAt() != null) {
            e.setVersion(d.version());
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}

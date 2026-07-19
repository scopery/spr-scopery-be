package com.company.scopery.modules.trust.classification.infrastructure.mapper;
import com.company.scopery.modules.trust.classification.domain.model.DataClassificationPolicy;
import com.company.scopery.modules.trust.classification.infrastructure.persistence.DataClassificationPolicyJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class DataClassificationPolicyPersistenceMapper {
    public DataClassificationPolicyJpaEntity toJpaEntity(DataClassificationPolicy d) {
        DataClassificationPolicyJpaEntity e = new DataClassificationPolicyJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setPolicyCode(d.policyCode());
        e.setName(d.name()); e.setDefaultClassification(d.defaultClassification());
        e.setEnabled(d.enabled()); e.setVersion(d.version()); e.setCreatedAt(d.createdAt());
        return e;
    }
    public DataClassificationPolicy toDomain(DataClassificationPolicyJpaEntity e) {
        return new DataClassificationPolicy(e.getId(), e.getWorkspaceId(), e.getPolicyCode(),
                e.getName(), e.getDefaultClassification(), e.isEnabled(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt());
    }
}

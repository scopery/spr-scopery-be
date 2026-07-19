package com.company.scopery.modules.raid.decision.infrastructure.mapper;
import com.company.scopery.modules.raid.decision.domain.model.DecisionOption;
import com.company.scopery.modules.raid.decision.infrastructure.persistence.DecisionOptionJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class DecisionOptionPersistenceMapper {
    public DecisionOption toDomain(DecisionOptionJpaEntity e) {
        return new DecisionOption(e.getId(), e.getDecisionId(), e.getProjectId(), e.getOptionTitle(), e.getOptionDescription(),
                e.getPros(), e.getCons(), e.getEstimatedImpact(), e.isSelectedFlag(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt());
    }
    public DecisionOptionJpaEntity toJpaEntity(DecisionOption d) {
        DecisionOptionJpaEntity e = new DecisionOptionJpaEntity();
        e.setId(d.id()); e.setDecisionId(d.decisionId()); e.setProjectId(d.projectId()); e.setOptionTitle(d.optionTitle());
        e.setOptionDescription(d.optionDescription()); e.setPros(d.pros()); e.setCons(d.cons());
        e.setEstimatedImpact(d.estimatedImpact()); e.setSelectedFlag(d.selectedFlag()); e.setVersion(d.version());
        e.setCreatedAt(d.createdAt());
        return e;
    }
}

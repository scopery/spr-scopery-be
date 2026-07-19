package com.company.scopery.modules.raid.raiditem.infrastructure.mapper;
import com.company.scopery.modules.raid.raiditem.domain.enums.*;
import com.company.scopery.modules.raid.raiditem.domain.model.RaidItem;
import com.company.scopery.modules.raid.raiditem.infrastructure.persistence.RaidItemJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class RaidItemPersistenceMapper {
    public RaidItem toDomain(RaidItemJpaEntity e) {
        return new RaidItem(e.getId(), e.getProjectId(), e.getWorkspaceId(), RaidItemType.valueOf(e.getType()), e.getCode(),
                e.getTitle(), e.getDescription(), RaidItemStatus.valueOf(e.getStatus()), e.getOwnerUserId(), e.getSeverity(),
                e.getProbability()==null?null:RaidProbability.valueOf(e.getProbability()),
                e.getImpact()==null?null:RaidImpact.valueOf(e.getImpact()), e.getRiskScore(), e.getRiskScoreFormulaVersion(),
                e.getRiskResponseStrategy()==null?null:RiskResponseStrategy.valueOf(e.getRiskResponseStrategy()),
                e.getRiskTrigger(), e.getTargetResolutionDate(), e.getAssumptionStatement(), e.getValidationStatus(),
                e.getValidationOwner(), e.getValidationDueDate(), e.getImpactIfFalse(), e.getIssueCategory(), e.getImpactSummary(),
                e.getRootCause(), e.getResolutionPlan(), e.getResolutionDueDate(), e.getResolvedAt(), e.getResolvedBy(),
                e.getDependencyType(), e.getEscalationLevel()==null?null:RaidEscalationLevel.valueOf(e.getEscalationLevel()),
                e.getEscalationReason(), e.getEscalatedAt(), e.getEscalatedBy(), e.getLinkedChangeRequestId(), e.getOutcomeNote(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public RaidItemJpaEntity toJpaEntity(RaidItem d) {
        RaidItemJpaEntity e = new RaidItemJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setWorkspaceId(d.workspaceId()); e.setType(d.type().name());
        e.setCode(d.code()); e.setTitle(d.title()); e.setDescription(d.description()); e.setStatus(d.status().name());
        e.setOwnerUserId(d.ownerUserId()); e.setSeverity(d.severity());
        e.setProbability(d.probability()==null?null:d.probability().name());
        e.setImpact(d.impact()==null?null:d.impact().name()); e.setRiskScore(d.riskScore());
        e.setRiskScoreFormulaVersion(d.riskScoreFormulaVersion());
        e.setRiskResponseStrategy(d.riskResponseStrategy()==null?null:d.riskResponseStrategy().name());
        e.setRiskTrigger(d.riskTrigger()); e.setTargetResolutionDate(d.targetResolutionDate());
        e.setAssumptionStatement(d.assumptionStatement()); e.setValidationStatus(d.validationStatus());
        e.setValidationOwner(d.validationOwner()); e.setValidationDueDate(d.validationDueDate());
        e.setImpactIfFalse(d.impactIfFalse()); e.setIssueCategory(d.issueCategory()); e.setImpactSummary(d.impactSummary());
        e.setRootCause(d.rootCause()); e.setResolutionPlan(d.resolutionPlan()); e.setResolutionDueDate(d.resolutionDueDate());
        e.setResolvedAt(d.resolvedAt()); e.setResolvedBy(d.resolvedBy()); e.setDependencyType(d.dependencyType());
        e.setEscalationLevel(d.escalationLevel()==null?null:d.escalationLevel().name());
        e.setEscalationReason(d.escalationReason()); e.setEscalatedAt(d.escalatedAt()); e.setEscalatedBy(d.escalatedBy());
        e.setLinkedChangeRequestId(d.linkedChangeRequestId()); e.setOutcomeNote(d.outcomeNote()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}

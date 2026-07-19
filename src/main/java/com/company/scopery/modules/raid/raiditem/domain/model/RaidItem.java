package com.company.scopery.modules.raid.raiditem.domain.model;
import com.company.scopery.modules.raid.raiditem.domain.RiskScoreCalculator;
import com.company.scopery.modules.raid.raiditem.domain.enums.*;
import java.time.Instant; import java.time.LocalDate; import java.util.UUID;
public record RaidItem(
        UUID id, UUID projectId, UUID workspaceId, RaidItemType type, String code, String title, String description,
        RaidItemStatus status, UUID ownerUserId, String severity, RaidProbability probability, RaidImpact impact,
        Integer riskScore, String riskScoreFormulaVersion, RiskResponseStrategy riskResponseStrategy, String riskTrigger,
        LocalDate targetResolutionDate, String assumptionStatement, String validationStatus, UUID validationOwner,
        LocalDate validationDueDate, String impactIfFalse, String issueCategory, String impactSummary, String rootCause,
        String resolutionPlan, LocalDate resolutionDueDate, Instant resolvedAt, UUID resolvedBy, String dependencyType,
        RaidEscalationLevel escalationLevel, String escalationReason, Instant escalatedAt, UUID escalatedBy,
        UUID linkedChangeRequestId, String outcomeNote, int version, Instant createdAt, Instant updatedAt
) {
    public static RaidItem create(UUID projectId, UUID workspaceId, RaidItemType type, String code, String title,
                                  String description, UUID ownerUserId) {
        Instant now = Instant.now();
        return new RaidItem(UUID.randomUUID(), projectId, workspaceId, type, code, title, description,
                RaidItemStatus.OPEN, ownerUserId, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, 0, now, now);
    }
    public RaidItem withRiskFields(RaidProbability probability, RaidImpact impact, RiskResponseStrategy strategy, String trigger) {
        Integer score = RiskScoreCalculator.score(probability, impact);
        return copy(status, ownerUserId, severity, probability, impact, score,
                score == null ? riskScoreFormulaVersion : RiskScoreCalculator.FORMULA_VERSION,
                strategy, trigger, targetResolutionDate, assumptionStatement, validationStatus, validationOwner,
                validationDueDate, impactIfFalse, issueCategory, impactSummary, rootCause, resolutionPlan,
                resolutionDueDate, resolvedAt, resolvedBy, dependencyType, escalationLevel, escalationReason,
                escalatedAt, escalatedBy, linkedChangeRequestId, outcomeNote);
    }
    public RaidItem resolve(UUID actorId, String note) {
        return copy(RaidItemStatus.RESOLVED, ownerUserId, severity, probability, impact, riskScore, riskScoreFormulaVersion,
                riskResponseStrategy, riskTrigger, targetResolutionDate, assumptionStatement, validationStatus, validationOwner,
                validationDueDate, impactIfFalse, issueCategory, impactSummary, rootCause, resolutionPlan, resolutionDueDate,
                Instant.now(), actorId, dependencyType, escalationLevel, escalationReason, escalatedAt, escalatedBy,
                linkedChangeRequestId, note);
    }
    public RaidItem close() {
        return copy(RaidItemStatus.CLOSED, ownerUserId, severity, probability, impact, riskScore, riskScoreFormulaVersion,
                riskResponseStrategy, riskTrigger, targetResolutionDate, assumptionStatement, validationStatus, validationOwner,
                validationDueDate, impactIfFalse, issueCategory, impactSummary, rootCause, resolutionPlan, resolutionDueDate,
                resolvedAt, resolvedBy, dependencyType, escalationLevel, escalationReason, escalatedAt, escalatedBy,
                linkedChangeRequestId, outcomeNote);
    }
    public RaidItem reopen() {
        return copy(RaidItemStatus.OPEN, ownerUserId, severity, probability, impact, riskScore, riskScoreFormulaVersion,
                riskResponseStrategy, riskTrigger, targetResolutionDate, assumptionStatement, validationStatus, validationOwner,
                validationDueDate, impactIfFalse, issueCategory, impactSummary, rootCause, resolutionPlan, resolutionDueDate,
                null, null, dependencyType, null, null, null, null, linkedChangeRequestId, outcomeNote);
    }
    public RaidItem escalate(UUID actorId, RaidEscalationLevel level, String reason) {
        return copy(RaidItemStatus.ESCALATED, ownerUserId, severity, probability, impact, riskScore, riskScoreFormulaVersion,
                riskResponseStrategy, riskTrigger, targetResolutionDate, assumptionStatement, validationStatus, validationOwner,
                validationDueDate, impactIfFalse, issueCategory, impactSummary, rootCause, resolutionPlan, resolutionDueDate,
                resolvedAt, resolvedBy, dependencyType, level, reason, Instant.now(), actorId, linkedChangeRequestId, outcomeNote);
    }
    public RaidItem convertRiskToIssue() {
        if (type != RaidItemType.RISK) throw new IllegalStateException("Only RISK can convert to ISSUE");
        return new RaidItem(id, projectId, workspaceId, RaidItemType.ISSUE, code, title, description, RaidItemStatus.OPEN,
                ownerUserId, severity, null, impact, null, null, null, null, targetResolutionDate, null, null, null, null, null,
                issueCategory == null ? "OTHER" : issueCategory, impactSummary, rootCause, resolutionPlan, resolutionDueDate,
                null, null, null, null, null, null, null, linkedChangeRequestId, outcomeNote, version, createdAt, Instant.now());
    }
    public RaidItem withStatus(RaidItemStatus status) {
        return copy(status, ownerUserId, severity, probability, impact, riskScore, riskScoreFormulaVersion, riskResponseStrategy,
                riskTrigger, targetResolutionDate, assumptionStatement, validationStatus, validationOwner, validationDueDate,
                impactIfFalse, issueCategory, impactSummary, rootCause, resolutionPlan, resolutionDueDate, resolvedAt, resolvedBy,
                dependencyType, escalationLevel, escalationReason, escalatedAt, escalatedBy, linkedChangeRequestId, outcomeNote);
    }
    public RaidItem withLinkedChangeRequest(UUID crId) {
        return copy(status, ownerUserId, severity, probability, impact, riskScore, riskScoreFormulaVersion, riskResponseStrategy,
                riskTrigger, targetResolutionDate, assumptionStatement, validationStatus, validationOwner, validationDueDate,
                impactIfFalse, issueCategory, impactSummary, rootCause, resolutionPlan, resolutionDueDate, resolvedAt, resolvedBy,
                dependencyType, escalationLevel, escalationReason, escalatedAt, escalatedBy, crId, outcomeNote);
    }
    public RaidItem update(String title, String description, UUID ownerUserId, String severity, String issueCategory,
                           String impactSummary, String rootCause, String resolutionPlan, String dependencyType,
                           String assumptionStatement, String validationStatus) {
        return new RaidItem(id, projectId, workspaceId, type, code, title, description, status, ownerUserId, severity,
                probability, impact, riskScore, riskScoreFormulaVersion, riskResponseStrategy, riskTrigger, targetResolutionDate,
                assumptionStatement, validationStatus, validationOwner, validationDueDate, impactIfFalse, issueCategory,
                impactSummary, rootCause, resolutionPlan, resolutionDueDate, resolvedAt, resolvedBy, dependencyType,
                escalationLevel, escalationReason, escalatedAt, escalatedBy, linkedChangeRequestId, outcomeNote,
                version, createdAt, Instant.now());
    }
    private RaidItem copy(RaidItemStatus status, UUID ownerUserId, String severity, RaidProbability probability, RaidImpact impact,
                          Integer riskScore, String formula, RiskResponseStrategy strategy, String trigger, LocalDate targetResolutionDate,
                          String assumptionStatement, String validationStatus, UUID validationOwner, LocalDate validationDueDate,
                          String impactIfFalse, String issueCategory, String impactSummary, String rootCause, String resolutionPlan,
                          LocalDate resolutionDueDate, Instant resolvedAt, UUID resolvedBy, String dependencyType,
                          RaidEscalationLevel escalationLevel, String escalationReason, Instant escalatedAt, UUID escalatedBy,
                          UUID linkedChangeRequestId, String outcomeNote) {
        return new RaidItem(id, projectId, workspaceId, type, code, title, description, status, ownerUserId, severity,
                probability, impact, riskScore, formula, strategy, trigger, targetResolutionDate, assumptionStatement,
                validationStatus, validationOwner, validationDueDate, impactIfFalse, issueCategory, impactSummary, rootCause,
                resolutionPlan, resolutionDueDate, resolvedAt, resolvedBy, dependencyType, escalationLevel, escalationReason,
                escalatedAt, escalatedBy, linkedChangeRequestId, outcomeNote, version, createdAt, Instant.now());
    }
}

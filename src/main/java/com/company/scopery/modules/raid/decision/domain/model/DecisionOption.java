package com.company.scopery.modules.raid.decision.domain.model;
import java.time.Instant; import java.util.UUID;
public record DecisionOption(UUID id, UUID decisionId, UUID projectId, String optionTitle, String optionDescription,
        String pros, String cons, String estimatedImpact, boolean selectedFlag, int version, Instant createdAt) {
    public static DecisionOption create(UUID decisionId, UUID projectId, String title, String description, String pros, String cons, String impact) {
        return new DecisionOption(UUID.randomUUID(), decisionId, projectId, title, description, pros, cons, impact, false, 0, Instant.now());
    }
    public DecisionOption select() {
        return new DecisionOption(id, decisionId, projectId, optionTitle, optionDescription, pros, cons, estimatedImpact, true, version, createdAt);
    }
    public DecisionOption update(String title, String description, String pros, String cons, String impact) {
        return new DecisionOption(id, decisionId, projectId, title, description, pros, cons, impact, selectedFlag, version, createdAt);
    }
}

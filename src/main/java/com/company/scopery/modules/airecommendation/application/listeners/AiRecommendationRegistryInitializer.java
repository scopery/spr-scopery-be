package com.company.scopery.modules.airecommendation.application.listeners;

import com.company.scopery.modules.airecommendation.domain.enums.ActionKind;
import com.company.scopery.modules.airecommendation.domain.enums.BaselineImpact;
import com.company.scopery.modules.airecommendation.domain.enums.DetectorExecutionMethod;
import com.company.scopery.modules.airecommendation.domain.enums.DetectorStatus;
import com.company.scopery.modules.airecommendation.domain.enums.NbaStatus;
import com.company.scopery.modules.airecommendation.domain.enums.PackStatus;
import com.company.scopery.modules.airecommendation.domain.enums.PolicyStatus;
import com.company.scopery.modules.airecommendation.domain.enums.SchemaStatus;
import com.company.scopery.modules.airecommendation.domain.enums.SuggestionOperation;
import com.company.scopery.modules.airecommendation.domain.enums.SuggestionSeverity;
import com.company.scopery.modules.airecommendation.domain.model.AiRecommendationPolicy;
import com.company.scopery.modules.airecommendation.domain.model.NextBestActionDefinition;
import com.company.scopery.modules.airecommendation.domain.model.RecommendationDetectorDefinition;
import com.company.scopery.modules.airecommendation.domain.model.RecommendationPackDefinition;
import com.company.scopery.modules.airecommendation.domain.model.SuggestionSchemaDefinition;
import com.company.scopery.modules.airecommendation.domain.repository.AiRecommendationPolicyRepository;
import com.company.scopery.modules.airecommendation.domain.repository.NextBestActionDefinitionRepository;
import com.company.scopery.modules.airecommendation.domain.repository.RecommendationDetectorDefinitionRepository;
import com.company.scopery.modules.airecommendation.domain.repository.RecommendationPackDefinitionRepository;
import com.company.scopery.modules.airecommendation.domain.repository.SuggestionSchemaDefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Component
@Order(52)
public class AiRecommendationRegistryInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(AiRecommendationRegistryInitializer.class);

    private final SuggestionSchemaDefinitionRepository schemaRepository;
    private final RecommendationPackDefinitionRepository packRepository;
    private final RecommendationDetectorDefinitionRepository detectorRepository;
    private final NextBestActionDefinitionRepository nbaRepository;
    private final AiRecommendationPolicyRepository policyRepository;

    public AiRecommendationRegistryInitializer(
            SuggestionSchemaDefinitionRepository schemaRepository,
            RecommendationPackDefinitionRepository packRepository,
            RecommendationDetectorDefinitionRepository detectorRepository,
            NextBestActionDefinitionRepository nbaRepository,
            AiRecommendationPolicyRepository policyRepository) {
        this.schemaRepository = schemaRepository;
        this.packRepository = packRepository;
        this.detectorRepository = detectorRepository;
        this.nbaRepository = nbaRepository;
        this.policyRepository = policyRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        seedSchemas();
        seedPacks();
        seedDetectors();
        seedNextBestActions();
        seedDefaultPolicy();
        log.info("[AiRecommendationRegistrySeed] Registry seed complete");
    }

    // ── Schemas ──────────────────────────────────────────────────────────────

    private void seedSchemas() {
        seedSchema("TASK_MISSING_OWNER", 1, "TASK_MISSING_OWNER",
                SuggestionOperation.UPDATE, "TASK", null, false, BaselineImpact.POSSIBLE);
        seedSchema("TASK_MISSING_ESTIMATE", 1, "TASK_MISSING_ESTIMATE",
                SuggestionOperation.UPDATE, "TASK", null, false, BaselineImpact.NONE);
        seedSchema("TASK_BLOCKED_WITHOUT_MITIGATION", 1, "TASK_BLOCKED_WITHOUT_MITIGATION",
                SuggestionOperation.UPDATE, "TASK", null, false, BaselineImpact.POSSIBLE);
        seedSchema("MEETING_ACTION_MISSING_OWNER", 1, "MEETING_ACTION_MISSING_OWNER",
                SuggestionOperation.UPDATE, "MEETING_ACTION_ITEM", null, false, BaselineImpact.POSSIBLE);
        seedSchema("MEETING_ACTION_MISSING_DUE_DATE", 1, "MEETING_ACTION_MISSING_DUE_DATE",
                SuggestionOperation.UPDATE, "MEETING_ACTION_ITEM", null, false, BaselineImpact.NONE);
        seedSchema("PHASE21_PLANNING_PROPOSAL", 1, "PHASE21_PLANNING_PROPOSAL",
                SuggestionOperation.NO_CHANGE_INSIGHT, null, null, false, BaselineImpact.UNKNOWN);
    }

    private void seedSchema(String code, int version, String suggestionType,
                            SuggestionOperation operation, String targetEntityType,
                            String capabilityCode, boolean confirmationRequired,
                            BaselineImpact baselineImpact) {
        if (schemaRepository.existsByCodeAndVersion(code, version)) return;
        OffsetDateTime now = OffsetDateTime.now();
        schemaRepository.save(new SuggestionSchemaDefinition(
                UUID.randomUUID(), code, version, suggestionType, operation,
                targetEntityType, capabilityCode, confirmationRequired, baselineImpact,
                List.of(), null, SchemaStatus.ACTIVE, true, now, now, 0));
    }

    // ── Packs ─────────────────────────────────────────────────────────────────

    private void seedPacks() {
        seedPack("TASK_PLANNING_HYGIENE_V1", 1, "Task Planning Hygiene",
                List.of("TASK_MISSING_OWNER", "TASK_MISSING_ESTIMATE", "TASK_BLOCKED_WITHOUT_MITIGATION"),
                List.of("MANUAL", "CHAT", "SCHEDULED"), 60, 1440, 50);
        seedPack("MEETING_FOLLOW_UP_HYGIENE_V1", 1, "Meeting Follow-up Hygiene",
                List.of("MEETING_ACTION_MISSING_OWNER", "MEETING_ACTION_MISSING_DUE_DATE"),
                List.of("MANUAL", "CHAT"), 60, 1440, 20);
        seedPack("PHASE21_PLANNING_COMPAT_V1", 1, "Phase 21 Planning Compatibility",
                List.of(), List.of("MANUAL"), 0, 0, 100);
    }

    private void seedPack(String code, int version, String name,
                          List<String> detectorCodes, List<String> triggerModes,
                          int cooldownMinutes, int expiryMinutes, int maxPerRun) {
        if (packRepository.existsByCodeAndVersion(code, version)) return;
        OffsetDateTime now = OffsetDateTime.now();
        packRepository.save(new RecommendationPackDefinition(
                UUID.randomUUID(), code, version, name, null,
                detectorCodes, triggerModes, false,
                cooldownMinutes, expiryMinutes, maxPerRun,
                PackStatus.ACTIVE, now, now, 0));
    }

    // ── Detectors ─────────────────────────────────────────────────────────────

    private void seedDetectors() {
        seedDetector("TASK_MISSING_OWNER", 1, "TASK_PLANNING_HYGIENE_V1",
                "TASK_MISSING_OWNER", "TASK_MISSING_OWNER", 1,
                DetectorExecutionMethod.DETERMINISTIC,
                BigDecimal.ONE, SuggestionSeverity.WARNING, 1440, 60, false);
        seedDetector("TASK_MISSING_ESTIMATE", 1, "TASK_PLANNING_HYGIENE_V1",
                "TASK_MISSING_ESTIMATE", "TASK_MISSING_ESTIMATE", 1,
                DetectorExecutionMethod.DETERMINISTIC,
                BigDecimal.ONE, SuggestionSeverity.INFO, 1440, 60, false);
        seedDetector("TASK_BLOCKED_WITHOUT_MITIGATION", 1, "TASK_PLANNING_HYGIENE_V1",
                "TASK_BLOCKED_WITHOUT_MITIGATION", "TASK_BLOCKED_WITHOUT_MITIGATION", 1,
                DetectorExecutionMethod.HEURISTIC,
                new BigDecimal("0.9500"), SuggestionSeverity.HIGH, 720, 60, false);
        seedDetector("MEETING_ACTION_MISSING_OWNER", 1, "MEETING_FOLLOW_UP_HYGIENE_V1",
                "MEETING_ACTION_MISSING_OWNER", "MEETING_ACTION_MISSING_OWNER", 1,
                DetectorExecutionMethod.DETERMINISTIC,
                BigDecimal.ONE, SuggestionSeverity.WARNING, 1440, 60, false);
        seedDetector("MEETING_ACTION_MISSING_DUE_DATE", 1, "MEETING_FOLLOW_UP_HYGIENE_V1",
                "MEETING_ACTION_MISSING_DUE_DATE", "MEETING_ACTION_MISSING_DUE_DATE", 1,
                DetectorExecutionMethod.DETERMINISTIC,
                BigDecimal.ONE, SuggestionSeverity.INFO, 1440, 60, false);
        seedDetector("PHASE21_PLANNING_COMPAT", 1, "PHASE21_PLANNING_COMPAT_V1",
                "PHASE21_PLANNING_PROPOSAL", "PHASE21_PLANNING_PROPOSAL", 1,
                DetectorExecutionMethod.COMPATIBILITY_ADAPTER,
                new BigDecimal("0.7000"), SuggestionSeverity.INFO, 0, 0, false);
    }

    private void seedDetector(String code, int version, String packCode,
                               String suggestionType, String schemaCode, int schemaVersion,
                               DetectorExecutionMethod method, BigDecimal confidence,
                               SuggestionSeverity severity, int expiryMinutes, int cooldownMinutes,
                               boolean nonSuppressible) {
        if (detectorRepository.existsByCodeAndVersion(code, version)) return;
        OffsetDateTime now = OffsetDateTime.now();
        detectorRepository.save(new RecommendationDetectorDefinition(
                UUID.randomUUID(), code, version, packCode, suggestionType,
                schemaCode, schemaVersion, method, confidence, severity,
                expiryMinutes, cooldownMinutes, nonSuppressible,
                DetectorStatus.ACTIVE, now, now, 0));
    }

    // ── Next Best Actions ─────────────────────────────────────────────────────

    private void seedNextBestActions() {
        seedNba("OPEN_TARGET", 1, "Open Target", ActionKind.NAVIGATE,
                List.of(), "AI_RECOMMENDATION_VIEW", null);
        seedNba("VIEW_EVIDENCE", 1, "View Evidence", ActionKind.REVIEW,
                List.of(), "AI_RECOMMENDATION_VIEW", null);
        seedNba("EDIT_PROPOSAL", 1, "Edit Proposal", ActionKind.EDIT,
                List.of(), "AI_RECOMMENDATION_EDIT", null);
        seedNba("ACCEPT_SUGGESTION", 1, "Accept Suggestion", ActionKind.ACCEPT,
                List.of(), "AI_RECOMMENDATION_ACCEPT", null);
        seedNba("REJECT_SUGGESTION", 1, "Reject Suggestion", ActionKind.REJECT,
                List.of(), "AI_RECOMMENDATION_REJECT", null);
        seedNba("SUPPRESS_SUGGESTION", 1, "Suppress Suggestion", ActionKind.SUPPRESS,
                List.of(), "AI_RECOMMENDATION_SUPPRESS", null);
        seedNba("PREPARE_APPLY", 1, "Prepare Apply", ActionKind.PREPARE_APPLY,
                List.of(), "AI_RECOMMENDATION_ACCEPT", "RESERVED_PHASE44_APPLY");
    }

    private void seedNba(String code, int version, String label, ActionKind actionKind,
                         List<String> applicableTypes, String requiredAuthority, String phase44ToolCode) {
        if (nbaRepository.existsByCodeAndVersion(code, version)) return;
        OffsetDateTime now = OffsetDateTime.now();
        NbaStatus status = phase44ToolCode != null ? NbaStatus.RESERVED_PHASE44 : NbaStatus.ACTIVE;
        nbaRepository.save(new NextBestActionDefinition(
                UUID.randomUUID(), code, version, label, null,
                actionKind, applicableTypes, requiredAuthority, null,
                phase44ToolCode, null, "LOW", status, now, now, 0));
    }

    // ── Default Policy ────────────────────────────────────────────────────────

    private void seedDefaultPolicy() {
        if (policyRepository.existsByCode("PROJECT_RECOMMENDATION_MVP_V1")) return;
        OffsetDateTime now = OffsetDateTime.now();
        policyRepository.save(new AiRecommendationPolicy(
                UUID.randomUUID(),
                "PROJECT_RECOMMENDATION_MVP_V1",
                "Project Recommendation MVP",
                "Default MVP policy — task and meeting hygiene packs",
                PolicyStatus.ACTIVE,
                "PROJECT",
                List.of("MANUAL", "CHAT", "SCHEDULED"),
                List.of("TASK_PLANNING_HYGIENE_V1", "MEETING_FOLLOW_UP_HYGIENE_V1"),
                false,
                new BigDecimal("0.4000"),
                "INFO",
                60,
                100,
                false,
                now, now, 0));
        log.info("[AiRecommendationRegistrySeed] Seeded default policy PROJECT_RECOMMENDATION_MVP_V1");
    }
}

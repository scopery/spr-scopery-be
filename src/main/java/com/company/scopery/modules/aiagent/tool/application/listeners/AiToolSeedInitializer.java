package com.company.scopery.modules.aiagent.tool.application.listeners;

import com.company.scopery.modules.aiagent.tool.domain.enums.AiToolMutationType;
import com.company.scopery.modules.aiagent.tool.domain.model.AiTool;
import com.company.scopery.modules.aiagent.tool.domain.model.AiToolRepository;
import com.company.scopery.modules.aiagent.tool.domain.valueobject.AiToolCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Idempotent seed of governed AI tool registry codes for Phase 31 + Phases 37–40 AI-001.
 * Registration only — live LLM tool handlers remain stub/no-op via ExecuteAiToolAction.
 */
@Component
@Order(14)
public class AiToolSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(AiToolSeedInitializer.class);

    private final AiToolRepository toolRepository;

    public AiToolSeedInitializer(AiToolRepository toolRepository) {
        this.toolRepository = toolRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        int created = 0;
        for (SeedTool seed : SEEDS) {
            if (seedOne(seed)) {
                created++;
            }
        }
        log.info("[AiToolSeed] Tool registry seed complete — created {} new tools ({} cataloged)",
                created, SEEDS.size());
    }

    private boolean seedOne(SeedTool seed) {
        AiToolCode code = AiToolCode.of(seed.code());
        if (toolRepository.existsByCode(code)) {
            return false;
        }
        AiTool tool = AiTool.create(
                code,
                seed.name(),
                seed.description(),
                seed.category(),
                seed.mutationType(),
                seed.requiresHumanApproval());
        toolRepository.save(tool);
        return true;
    }

    private record SeedTool(
            String code,
            String name,
            String description,
            String category,
            AiToolMutationType mutationType,
            boolean requiresHumanApproval
    ) {}

    private static final List<SeedTool> SEEDS = List.of(
            // Phase 31 — Meeting collaboration
            seed("summarizeMeetingNotes", "Summarize Meeting Notes", "MEETING", false),
            seed("extractMeetingActionItems", "Extract Meeting Action Items", "MEETING", false),
            seed("extractMeetingDecisions", "Extract Meeting Decisions", "MEETING", false),
            seed("extractMeetingRisksIssues", "Extract Meeting Risks/Issues", "MEETING", false),
            seed("draftMeetingMinutes", "Draft Meeting Minutes", "MEETING", false),
            seed("draftFollowUpMessage", "Draft Follow-up Message", "MEETING", false),
            seed("suggestMeetingAgenda", "Suggest Meeting Agenda", "MEETING", false),

            // Phase 37 — Resource capacity AI-001
            seed("suggestUnderallocatedRoles", "Suggest Underallocated Roles", "RESOURCE_CAPACITY", false),
            seed("suggestOverloadedResources", "Suggest Overloaded Resources", "RESOURCE_CAPACITY", false),
            seed("suggestAssignmentRebalance", "Suggest Assignment Rebalance", "RESOURCE_CAPACITY", false),
            seed("explainEffortForecastIncrease", "Explain Effort Forecast Increase", "RESOURCE_CAPACITY", false),
            seed("explainTaskCostOverrun", "Explain Task Cost Overrun", "RESOURCE_CAPACITY", false),
            seed("draftStaffingRecommendation", "Draft Staffing Recommendation", "RESOURCE_CAPACITY", false),

            // Phase 38 — Trust / compliance AI-001
            seed("summarizeAccessReviewFindings", "Summarize Access Review Findings", "TRUST_COMPLIANCE", false),
            seed("summarizeSensitiveAccessActivity", "Summarize Sensitive Access Activity", "TRUST_COMPLIANCE", false),
            seed("draftPrivacyRequestResponse", "Draft Privacy Request Response", "TRUST_COMPLIANCE", false),
            seed("explainRetentionCandidates", "Explain Retention Candidates", "TRUST_COMPLIANCE", false),
            seed("summarizeExportAudit", "Summarize Export Audit", "TRUST_COMPLIANCE", false),
            seed("detectSuspiciousAccessPatterns", "Detect Suspicious Access Patterns", "TRUST_COMPLIANCE", false),

            // Phase 39 — Integration hub AI-001
            seed("suggestCsvFieldMapping", "Suggest CSV Field Mapping", "INTEGRATION", false),
            seed("summarizeImportErrors", "Summarize Import Errors", "INTEGRATION", false),
            seed("suggestSyncConflictResolution", "Suggest Sync Conflict Resolution", "INTEGRATION", false),
            seed("explainProviderFailure", "Explain Provider Failure", "INTEGRATION", false),
            seed("suggestExportColumns", "Suggest Export Columns", "INTEGRATION", false),
            seed("summarizeIntegrationHealth", "Summarize Integration Health", "INTEGRATION", false),

            // Phase 40 — Service support AI-001
            seed("summarizeSupportCase", "Summarize Support Case", "SERVICE_SUPPORT", false),
            seed("suggestCaseTypePriority", "Suggest Case Type/Priority", "SERVICE_SUPPORT", false),
            seed("detectDuplicateCases", "Detect Duplicate Cases", "SERVICE_SUPPORT", false),
            seed("suggestKnowledgeArticle", "Suggest Knowledge Article", "SERVICE_SUPPORT", false),
            seed("summarizeIncidentTimeline", "Summarize Incident Timeline", "SERVICE_SUPPORT", false),
            seed("draftClientUpdate", "Draft Client Update", "SERVICE_SUPPORT", false),
            seed("explainSlaBreach", "Explain SLA Breach", "SERVICE_SUPPORT", false),
            seed("suggestSupportNextAction", "Suggest Support Next Action", "SERVICE_SUPPORT", false),

            // Phase 41 — Knowledge semantic retrieval
            seed("knowledge.search", "Knowledge Semantic Search", "KNOWLEDGE", false)
    );

    private static SeedTool seed(String code, String name, String category, boolean requiresApproval) {
        return new SeedTool(
                code,
                name,
                "Seeded AI-001 tool registration — execution is stub/no-op until live handlers exist",
                category,
                AiToolMutationType.READ,
                requiresApproval);
    }
}

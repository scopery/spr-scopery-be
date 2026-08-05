package com.company.scopery.modules.traceability.aimapping.application.action;

import com.company.scopery.modules.traceability.aimapping.application.command.ApplyMappingDraftCommand;
import com.company.scopery.modules.traceability.aimapping.application.response.ApplyMappingDraftResponse;
import com.company.scopery.modules.traceability.aimapping.run.domain.model.MappingRunRepository;
import com.company.scopery.modules.traceability.aimapping.shared.activity.AiMappingActivityLogger;
import com.company.scopery.modules.traceability.aimapping.shared.constant.AiMappingActivityActions;
import com.company.scopery.modules.traceability.aimapping.shared.constant.AiMappingEntityTypes;
import com.company.scopery.modules.traceability.aimapping.shared.error.AiMappingExceptions;
import com.company.scopery.modules.traceability.aimapping.suggestion.domain.enums.SuggestionDecision;
import com.company.scopery.modules.traceability.aimapping.suggestion.domain.enums.SuggestionReviewStatus;
import com.company.scopery.modules.traceability.aimapping.suggestion.domain.model.MappingSuggestion;
import com.company.scopery.modules.traceability.aimapping.suggestion.domain.model.MappingSuggestionRepository;
import com.company.scopery.modules.traceability.aimapping.summary.domain.enums.SummaryEntityType;
import com.company.scopery.modules.traceability.tracelink.domain.enums.TraceLinkType;
import com.company.scopery.modules.traceability.tracelink.domain.model.TraceLink;
import com.company.scopery.modules.traceability.tracelink.domain.model.TraceLinkRepository;
import com.company.scopery.modules.traceability.usecase.domain.model.RequirementFunctionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Types;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ApplyMappingDraftAction {

    private static final Logger log = LoggerFactory.getLogger(ApplyMappingDraftAction.class);

    private final MappingRunRepository runRepository;
    private final MappingSuggestionRepository suggestionRepository;
    private final RequirementFunctionRepository requirementFunctionRepository;
    private final TraceLinkRepository traceLinkRepository;
    private final JdbcTemplate jdbc;
    private final AiMappingActivityLogger activityLogger;

    public ApplyMappingDraftAction(MappingRunRepository runRepository,
                                   MappingSuggestionRepository suggestionRepository,
                                   RequirementFunctionRepository requirementFunctionRepository,
                                   TraceLinkRepository traceLinkRepository,
                                   JdbcTemplate jdbc,
                                   AiMappingActivityLogger activityLogger) {
        this.runRepository = runRepository;
        this.suggestionRepository = suggestionRepository;
        this.requirementFunctionRepository = requirementFunctionRepository;
        this.traceLinkRepository = traceLinkRepository;
        this.jdbc = jdbc;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ApplyMappingDraftResponse execute(ApplyMappingDraftCommand command) {
        var run = runRepository.findById(command.runId())
                .orElseThrow(() -> AiMappingExceptions.runNotFound(command.runId()));

        List<MappingSuggestion> accepted = suggestionRepository.findAcceptedByRunId(command.runId());

        int created = 0;
        int skippedStale = 0;
        int skippedConflict = 0;
        int failed = 0;
        List<UUID> expiredIds = new ArrayList<>();

        for (MappingSuggestion suggestion : accepted) {
            if (suggestion.decision() == SuggestionDecision.NO_MATCH || suggestion.targetId() == null) {
                continue;
            }

            // Staleness check: if source entity was updated since the suggestion was generated,
            // mark it EXPIRED instead of applying — stale suggestions may map to a changed entity.
            if (suggestion.sourceVersion() != null) {
                int currentVersion = loadCurrentEntityVersion(suggestion.sourceType(), suggestion.sourceId());
                if (currentVersion > suggestion.sourceVersion()) {
                    expiredIds.add(suggestion.id());
                    skippedStale++;
                    continue;
                }
            }

            try {
                boolean applied = applySuggestion(suggestion, run.projectId());
                if (applied) {
                    created++;
                } else {
                    skippedConflict++;
                }
            } catch (Exception e) {
                log.warn("Failed to apply suggestion {}: {}", suggestion.id(), e.getMessage());
                failed++;
            }
        }

        if (!expiredIds.isEmpty()) {
            suggestionRepository.bulkUpdateReviewStatus(expiredIds, SuggestionReviewStatus.EXPIRED, null, Instant.now());
            log.info("Marked {} stale suggestions as EXPIRED for run {}", expiredIds.size(), command.runId());
        }

        if (created > 0) {
            activityLogger.logSuccess(
                    AiMappingEntityTypes.MAPPING_RUN, command.runId(),
                    AiMappingActivityActions.APPLY_MAPPING_DRAFT,
                    "Applied " + created + " mapping(s) from run " + command.runId());
        }

        return new ApplyMappingDraftResponse(created, skippedStale, skippedConflict, failed);
    }

    private boolean applySuggestion(MappingSuggestion suggestion, UUID projectId) {
        return switch (suggestion.relationType()) {
            case REQUIREMENT_TO_FUNCTION -> applyRequirementFunction(suggestion, projectId);
            case FUNCTION_TO_USE_CASE    -> applyUseCaseFunction(suggestion);
            case USE_CASE_TO_TEST_CASE   -> applyTestCaseUseCase(suggestion);
        };
    }

    private boolean applyRequirementFunction(MappingSuggestion s, UUID projectId) {
        if (requirementFunctionRepository.exists(s.sourceId(), s.targetId())) return false;
        requirementFunctionRepository.link(s.sourceId(), s.targetId());
        if (!traceLinkRepository.existsActiveLink(projectId, "REQUIREMENT", s.sourceId(),
                "FUNCTIONAL_ITEM", s.targetId(), TraceLinkType.COVERS.name())) {
            traceLinkRepository.save(TraceLink.create(projectId, "REQUIREMENT", s.sourceId(),
                    "FUNCTIONAL_ITEM", s.targetId(), TraceLinkType.COVERS,
                    null, null, null, null));
        }
        return true;
    }

    private boolean applyUseCaseFunction(MappingSuggestion s) {
        List<Object> result = jdbc.queryForList(
                "SELECT primary_function_id FROM app_use_case WHERE id = ?::uuid",
                Object.class, s.sourceId().toString());
        if (!result.isEmpty() && result.get(0) != null) return false;
        jdbc.update("UPDATE app_use_case SET primary_function_id = ?::uuid, updated_at = ? WHERE id = ?::uuid",
                new Object[]{s.targetId().toString(), Instant.now(), s.sourceId().toString()},
                new int[]{Types.VARCHAR, Types.TIMESTAMP, Types.VARCHAR});
        return true;
    }

    private boolean applyTestCaseUseCase(MappingSuggestion s) {
        List<Object> result = jdbc.queryForList(
                "SELECT use_case_id FROM quality_test_case WHERE id = ?::uuid",
                Object.class, s.sourceId().toString());
        if (!result.isEmpty() && result.get(0) != null) return false;
        jdbc.update("UPDATE quality_test_case SET use_case_id = ?::uuid, updated_at = ? WHERE id = ?::uuid",
                new Object[]{s.targetId().toString(), Instant.now(), s.sourceId().toString()},
                new int[]{Types.VARCHAR, Types.TIMESTAMP, Types.VARCHAR});
        return true;
    }

    private int loadCurrentEntityVersion(SummaryEntityType type, UUID id) {
        String sql = switch (type) {
            case REQUIREMENT -> "SELECT COALESCE(version, 0) FROM requirements_requirement WHERE id = ?::uuid";
            case FUNCTION    -> "SELECT COALESCE(version, 0) FROM app_functional_item WHERE id = ?::uuid";
            case USE_CASE    -> "SELECT COALESCE(version, 0) FROM app_use_case WHERE id = ?::uuid";
            case TEST_CASE   -> "SELECT COALESCE(version, 0) FROM quality_test_case WHERE id = ?::uuid";
        };
        List<Integer> rows = jdbc.queryForList(sql, Integer.class, id.toString());
        return rows.isEmpty() ? -1 : rows.get(0);
    }
}

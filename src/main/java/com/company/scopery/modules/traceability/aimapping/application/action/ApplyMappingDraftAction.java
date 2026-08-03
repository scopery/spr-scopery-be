package com.company.scopery.modules.traceability.aimapping.application.action;

import com.company.scopery.modules.traceability.aimapping.application.command.ApplyMappingDraftCommand;
import com.company.scopery.modules.traceability.aimapping.application.response.ApplyMappingDraftResponse;
import com.company.scopery.modules.traceability.aimapping.run.domain.enums.MappingRelationType;
import com.company.scopery.modules.traceability.aimapping.run.domain.model.MappingRunRepository;
import com.company.scopery.modules.traceability.aimapping.shared.activity.AiMappingActivityLogger;
import com.company.scopery.modules.traceability.aimapping.shared.constant.AiMappingActivityActions;
import com.company.scopery.modules.traceability.aimapping.shared.constant.AiMappingEntityTypes;
import com.company.scopery.modules.traceability.aimapping.shared.error.AiMappingExceptions;
import com.company.scopery.modules.traceability.aimapping.suggestion.domain.enums.SuggestionDecision;
import com.company.scopery.modules.traceability.aimapping.suggestion.domain.model.MappingSuggestion;
import com.company.scopery.modules.traceability.aimapping.suggestion.domain.model.MappingSuggestionRepository;
import com.company.scopery.modules.traceability.usecase.domain.model.RequirementFunctionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Types;
import java.time.Instant;
import java.util.List;

@Component
public class ApplyMappingDraftAction {

    private static final Logger log = LoggerFactory.getLogger(ApplyMappingDraftAction.class);

    private final MappingRunRepository runRepository;
    private final MappingSuggestionRepository suggestionRepository;
    private final RequirementFunctionRepository requirementFunctionRepository;
    private final JdbcTemplate jdbc;
    private final AiMappingActivityLogger activityLogger;

    public ApplyMappingDraftAction(MappingRunRepository runRepository,
                                   MappingSuggestionRepository suggestionRepository,
                                   RequirementFunctionRepository requirementFunctionRepository,
                                   JdbcTemplate jdbc,
                                   AiMappingActivityLogger activityLogger) {
        this.runRepository = runRepository;
        this.suggestionRepository = suggestionRepository;
        this.requirementFunctionRepository = requirementFunctionRepository;
        this.jdbc = jdbc;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ApplyMappingDraftResponse execute(ApplyMappingDraftCommand command) {
        runRepository.findById(command.runId())
                .orElseThrow(() -> AiMappingExceptions.runNotFound(command.runId()));

        List<MappingSuggestion> accepted = suggestionRepository.findAcceptedByRunId(command.runId());

        int created = 0;
        int skippedStale = 0;
        int skippedConflict = 0;
        int failed = 0;

        for (MappingSuggestion suggestion : accepted) {
            if (suggestion.decision() == SuggestionDecision.NO_MATCH || suggestion.targetId() == null) {
                continue;
            }
            try {
                boolean applied = applySuggestion(suggestion);
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

        if (created > 0) {
            activityLogger.logSuccess(
                    AiMappingEntityTypes.MAPPING_RUN, command.runId(),
                    AiMappingActivityActions.APPLY_MAPPING_DRAFT,
                    "Applied " + created + " mapping(s) from run " + command.runId());
        }

        return new ApplyMappingDraftResponse(created, skippedStale, skippedConflict, failed);
    }

    private boolean applySuggestion(MappingSuggestion suggestion) {
        MappingRelationType relationType = suggestion.relationType();
        return switch (relationType) {
            case REQUIREMENT_TO_FUNCTION -> applyRequirementFunction(suggestion);
            case FUNCTION_TO_USE_CASE -> applyUseCaseFunction(suggestion);
            case USE_CASE_TO_TEST_CASE -> applyTestCaseUseCase(suggestion);
        };
    }

    private boolean applyRequirementFunction(MappingSuggestion s) {
        if (requirementFunctionRepository.exists(s.sourceId(), s.targetId())) {
            return false;
        }
        requirementFunctionRepository.link(s.sourceId(), s.targetId());
        return true;
    }

    private boolean applyUseCaseFunction(MappingSuggestion s) {
        String checkSql = "SELECT primary_function_id FROM app_use_case WHERE id = ?::uuid";
        List<Object> result = jdbc.queryForList(checkSql, Object.class, s.sourceId().toString());
        if (!result.isEmpty() && result.get(0) != null) {
            return false;
        }
        jdbc.update("UPDATE app_use_case SET primary_function_id = ?::uuid, updated_at = ? WHERE id = ?::uuid",
                new Object[]{s.targetId().toString(), Instant.now(), s.sourceId().toString()},
                new int[]{Types.VARCHAR, Types.TIMESTAMP, Types.VARCHAR});
        return true;
    }

    private boolean applyTestCaseUseCase(MappingSuggestion s) {
        String checkSql = "SELECT use_case_id FROM quality_test_case WHERE id = ?::uuid";
        List<Object> result = jdbc.queryForList(checkSql, Object.class, s.sourceId().toString());
        if (!result.isEmpty() && result.get(0) != null) {
            return false;
        }
        jdbc.update("UPDATE quality_test_case SET use_case_id = ?::uuid, updated_at = ? WHERE id = ?::uuid",
                new Object[]{s.targetId().toString(), Instant.now(), s.sourceId().toString()},
                new int[]{Types.VARCHAR, Types.TIMESTAMP, Types.VARCHAR});
        return true;
    }
}

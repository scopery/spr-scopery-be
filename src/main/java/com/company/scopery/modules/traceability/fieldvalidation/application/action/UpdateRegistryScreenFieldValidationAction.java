package com.company.scopery.modules.traceability.fieldvalidation.application.action;

import com.company.scopery.modules.traceability.fieldvalidation.application.command.UpdateRegistryScreenFieldValidationCommand;
import com.company.scopery.modules.traceability.fieldvalidation.application.response.RegistryScreenFieldValidationResponse;
import com.company.scopery.modules.traceability.fieldvalidation.domain.model.RegistryScreenFieldValidation;
import com.company.scopery.modules.traceability.fieldvalidation.domain.model.RegistryScreenFieldValidationRepository;
import com.company.scopery.modules.traceability.screenfield.domain.model.RegistryScreenFieldRepository;
import com.company.scopery.modules.traceability.screenmode.domain.model.RegistryScreenMode;
import com.company.scopery.modules.traceability.screenmode.domain.model.RegistryScreenModeRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.validationruletype.domain.model.RegistryValidationRuleType;
import com.company.scopery.modules.traceability.validationruletype.domain.model.RegistryValidationRuleTypeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateRegistryScreenFieldValidationAction {

    private final RegistryScreenFieldValidationRepository repo;
    private final RegistryScreenFieldRepository fieldRepo;
    private final RegistryValidationRuleTypeRepository ruleTypeRepo;
    private final RegistryScreenModeRepository modeRepo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;
    private final ObjectMapper objectMapper;

    public UpdateRegistryScreenFieldValidationAction(
            RegistryScreenFieldValidationRepository repo,
            RegistryScreenFieldRepository fieldRepo,
            RegistryValidationRuleTypeRepository ruleTypeRepo,
            RegistryScreenModeRepository modeRepo,
            TraceabilityAuthorizationService authorization,
            TraceabilityActivityLogger activityLogger,
            ObjectMapper objectMapper) {
        this.repo = repo;
        this.fieldRepo = fieldRepo;
        this.ruleTypeRepo = ruleTypeRepo;
        this.modeRepo = modeRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public RegistryScreenFieldValidationResponse execute(UpdateRegistryScreenFieldValidationCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());

        // IDOR check: verify field belongs to the specified screen and workspace
        var field = fieldRepo.findByIdAndWorkspaceId(c.fieldId(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.screenFieldNotFound(c.fieldId()));
        if (!field.screenId().equals(c.screenId())) {
            throw TraceabilityExceptions.screenFieldNotFound(c.fieldId());
        }

        // Load the validation record and verify it belongs to this field
        RegistryScreenFieldValidation existing = repo.findByIdAndWorkspaceId(c.validationId(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.fieldValidationNotFound(c.validationId()));
        if (!existing.fieldId().equals(c.fieldId())) {
            throw TraceabilityExceptions.fieldValidationNotFound(c.validationId());
        }

        // Load the rule type (for param validation)
        RegistryValidationRuleType ruleType = ruleTypeRepo.findByIdAndAccessible(existing.ruleTypeId(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.validationRuleTypeNotFound(existing.ruleTypeId()));

        // If modeId provided: verify mode exists, belongs to same workspace and screen, is ACTIVE
        if (c.modeId() != null) {
            RegistryScreenMode mode = modeRepo.findByIdAndWorkspaceId(c.modeId(), c.workspaceId())
                    .orElseThrow(() -> TraceabilityExceptions.screenModeNotFound(c.modeId()));
            if (!mode.screenId().equals(c.screenId())) {
                throw TraceabilityExceptions.screenModeWrongScreen(c.modeId());
            }
            if (!mode.status().name().equals("ACTIVE")) {
                throw TraceabilityExceptions.screenModeInactive(c.modeId());
            }
        }

        // Validate rule_param_json structure
        validateRuleParam(ruleType, c.ruleParamJson());

        RegistryScreenFieldValidation updated = repo.save(
                existing.withUpdated(c.modeId(), c.ruleParamJson(), c.conditionJson(),
                        c.errorMessage(), c.remark(), c.displayOrder()));

        activityLogger.logSuccess(TraceabilityEntityTypes.SCREEN_FIELD_VALIDATION, updated.id(),
                TraceabilityActivityActions.FIELD_VALIDATION_UPDATED,
                "Field validation updated: " + c.validationId());

        return RegistryScreenFieldValidationResponse.from(updated);
    }

    private void validateRuleParam(RegistryValidationRuleType ruleType, String ruleParamJson) {
        if (ruleType.paramSchemaJson() == null || ruleType.paramSchemaJson().isBlank()) {
            if (ruleParamJson != null && !ruleParamJson.isBlank()) {
                throw TraceabilityExceptions.fieldValidationRuleParamInvalid(
                        "Rule type '" + ruleType.code() + "' does not accept rule parameters");
            }
            return;
        }
        if (ruleParamJson == null || ruleParamJson.isBlank()) {
            throw TraceabilityExceptions.fieldValidationRuleParamInvalid(
                    "Rule type '" + ruleType.code() + "' requires rule parameters");
        }
        try {
            objectMapper.readTree(ruleParamJson);
        } catch (Exception e) {
            throw TraceabilityExceptions.fieldValidationRuleParamInvalid("rule_param_json is not valid JSON");
        }
    }
}

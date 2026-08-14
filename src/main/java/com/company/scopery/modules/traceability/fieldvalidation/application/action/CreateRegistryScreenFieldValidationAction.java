package com.company.scopery.modules.traceability.fieldvalidation.application.action;

import com.company.scopery.modules.traceability.fieldvalidation.application.command.CreateRegistryScreenFieldValidationCommand;
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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Component
public class CreateRegistryScreenFieldValidationAction {

    private final RegistryScreenFieldValidationRepository repo;
    private final RegistryScreenFieldRepository fieldRepo;
    private final RegistryValidationRuleTypeRepository ruleTypeRepo;
    private final RegistryScreenModeRepository modeRepo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;
    private final ObjectMapper objectMapper;

    public CreateRegistryScreenFieldValidationAction(
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
    public RegistryScreenFieldValidationResponse execute(CreateRegistryScreenFieldValidationCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());

        // IDOR check: verify field belongs to the specified screen and workspace
        var field = fieldRepo.findByIdAndWorkspaceId(c.fieldId(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.screenFieldNotFound(c.fieldId()));
        if (!field.screenId().equals(c.screenId())) {
            throw TraceabilityExceptions.screenFieldNotFound(c.fieldId());
        }

        // Verify rule type is accessible (workspace-specific or system)
        RegistryValidationRuleType ruleType = ruleTypeRepo.findByIdAndAccessible(c.ruleTypeId(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.validationRuleTypeNotFound(c.ruleTypeId()));

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

        RegistryScreenFieldValidation saved = repo.save(
                RegistryScreenFieldValidation.create(
                        c.fieldId(), c.modeId(), c.ruleTypeId(), c.workspaceId(),
                        c.ruleParamJson(), c.conditionJson(), c.errorMessage(),
                        c.remark(), c.displayOrder()));

        activityLogger.logSuccess(TraceabilityEntityTypes.SCREEN_FIELD_VALIDATION, saved.id(),
                TraceabilityActivityActions.FIELD_VALIDATION_CREATED,
                "Field validation created for field: " + c.fieldId());

        return RegistryScreenFieldValidationResponse.from(saved);
    }

    private void validateRuleParam(RegistryValidationRuleType ruleType, String ruleParamJson) {
        String schemaJson = ruleType.paramSchemaJson();
        if (schemaJson == null || schemaJson.isBlank()) {
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
        JsonNode param;
        JsonNode schema;
        try {
            param = objectMapper.readTree(ruleParamJson);
            schema = objectMapper.readTree(schemaJson);
        } catch (Exception e) {
            throw TraceabilityExceptions.fieldValidationRuleParamInvalid("rule_param_json is not valid JSON");
        }
        // Validate each key declared in schema exists in param with correct type
        schema.fields().forEachRemaining(entry -> {
            String key = entry.getKey();
            String expectedType = entry.getValue().isTextual() ? entry.getValue().asText() : "any";
            JsonNode value = param.get(key);
            if (value == null || value.isNull()) {
                throw TraceabilityExceptions.fieldValidationRuleParamInvalid(
                        "Missing required param key: '" + key + "'");
            }
            validateParamValue(ruleType.code(), key, value, expectedType);
        });
    }

    private void validateParamValue(String ruleCode, String key, JsonNode value, String expectedType) {
        switch (expectedType) {
            case "integer" -> {
                if (!value.isInt() && !value.isLong()) {
                    throw TraceabilityExceptions.fieldValidationRuleParamInvalid(
                            "Param '" + key + "' must be an integer");
                }
            }
            case "string" -> {
                if (!value.isTextual()) {
                    throw TraceabilityExceptions.fieldValidationRuleParamInvalid(
                            "Param '" + key + "' must be a string");
                }
                if ("pattern".equals(key) && "REGEX".equals(ruleCode)) {
                    try { Pattern.compile(value.asText()); }
                    catch (Exception e) {
                        throw TraceabilityExceptions.fieldValidationRuleParamInvalid(
                                "Param 'pattern' is not a valid regular expression: " + e.getMessage());
                    }
                }
            }
            default -> { /* no type enforcement for complex types like arrays */ }
        }
    }
}

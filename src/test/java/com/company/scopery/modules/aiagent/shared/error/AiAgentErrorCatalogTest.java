package com.company.scopery.modules.aiagent.shared.error;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class AiAgentErrorCatalogTest {

    @Test
    void notFoundEntries_haveHttpStatus404() {
        assertThat(AiAgentErrorCatalog.PROVIDER_NOT_FOUND.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(AiAgentErrorCatalog.AI_MODEL_NOT_FOUND.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(AiAgentErrorCatalog.MODEL_DEPLOYMENT_NOT_FOUND.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(AiAgentErrorCatalog.MODEL_PARAMETER_CAPABILITY_NOT_FOUND.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(AiAgentErrorCatalog.AGENT_NOT_FOUND.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(AiAgentErrorCatalog.PROMPT_TEMPLATE_NOT_FOUND.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(AiAgentErrorCatalog.PROMPT_VERSION_NOT_FOUND.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void conflictEntries_haveHttpStatus409() {
        assertThat(AiAgentErrorCatalog.PROVIDER_CODE_ALREADY_EXISTS.httpStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(AiAgentErrorCatalog.AI_MODEL_CODE_ALREADY_EXISTS.httpStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(AiAgentErrorCatalog.MODEL_DEPLOYMENT_CODE_ALREADY_EXISTS.httpStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(AiAgentErrorCatalog.MODEL_PARAMETER_CAPABILITY_ALREADY_EXISTS.httpStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(AiAgentErrorCatalog.AGENT_CODE_ALREADY_EXISTS.httpStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(AiAgentErrorCatalog.PROMPT_TEMPLATE_CODE_ALREADY_EXISTS.httpStatus()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void invalidInputEntries_haveHttpStatus400() {
        assertThat(AiAgentErrorCatalog.INVALID_MODEL_DEPLOYMENT_ENVIRONMENT.httpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(AiAgentErrorCatalog.INVALID_AGENT_TYPE.httpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(AiAgentErrorCatalog.INVALID_AGENT_OUTPUT_FORMAT.httpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(AiAgentErrorCatalog.INVALID_PROMPT_CONTENT_FORMAT.httpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(AiAgentErrorCatalog.INVALID_PROMPT_CONTENT_JSON.httpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(AiAgentErrorCatalog.INVALID_MODEL_PARAMETER_IF_NULL_BEHAVIOR.httpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void businessRuleEntries_haveHttpStatus422() {
        assertThat(AiAgentErrorCatalog.DEPRECATED_PROVIDER_CANNOT_BE_ACTIVATED.httpStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(AiAgentErrorCatalog.DEPRECATED_AGENT_CANNOT_BE_ACTIVATED.httpStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(AiAgentErrorCatalog.DEPRECATED_PROMPT_TEMPLATE_CANNOT_BE_ACTIVATED.httpStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(AiAgentErrorCatalog.ARCHIVED_PROMPT_VERSION_CANNOT_BE_ACTIVATED.httpStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(AiAgentErrorCatalog.PROMPT_VERSION_CONTENT_NOT_EDITABLE.httpStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(AiAgentErrorCatalog.AI_MODEL_PROVIDER_NOT_ACTIVE.httpStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void eachEntry_hasNonBlankCodeAndMessage() {
        for (AiAgentErrorCatalog entry : AiAgentErrorCatalog.values()) {
            assertThat(entry.code()).as(entry.name() + ".code").isNotBlank();
            assertThat(entry.defaultMessage()).as(entry.name() + ".defaultMessage").isNotBlank();
            assertThat(entry.httpStatus()).as(entry.name() + ".httpStatus").isNotNull();
        }
    }

    @Test
    void providerNotFound_hasCorrectValues() {
        AiAgentErrorCatalog entry = AiAgentErrorCatalog.PROVIDER_NOT_FOUND;

        assertThat(entry.code()).isEqualTo("PROVIDER_NOT_FOUND");
        assertThat(entry.defaultMessage()).isEqualTo("Provider not found");
        assertThat(entry.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void agentCodeAlreadyExists_hasCorrectValues() {
        AiAgentErrorCatalog entry = AiAgentErrorCatalog.AGENT_CODE_ALREADY_EXISTS;

        assertThat(entry.code()).isEqualTo("AGENT_CODE_ALREADY_EXISTS");
        assertThat(entry.httpStatus()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void eventConfigNotFoundEntries_haveHttpStatus404() {
        assertThat(AiAgentErrorCatalog.EVENT_CONFIG_NOT_FOUND.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(AiAgentErrorCatalog.ACTIVE_EVENT_CONFIG_NOT_FOUND.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(AiAgentErrorCatalog.EVENT_CONFIG_EVENT_DEFINITION_NOT_FOUND.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(AiAgentErrorCatalog.EVENT_CONFIG_AGENT_NOT_FOUND.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(AiAgentErrorCatalog.EVENT_CONFIG_PROMPT_VERSION_NOT_FOUND.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(AiAgentErrorCatalog.EVENT_CONFIG_PROMPT_TEMPLATE_NOT_FOUND.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(AiAgentErrorCatalog.EVENT_CONFIG_MODEL_DEPLOYMENT_NOT_FOUND.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void eventConfigConflictEntries_haveHttpStatus409() {
        assertThat(AiAgentErrorCatalog.EVENT_CONFIG_CODE_ALREADY_EXISTS.httpStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(AiAgentErrorCatalog.EVENT_CONFIG_ACTIVE_ALREADY_EXISTS.httpStatus()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void eventConfigBusinessRuleEntries_haveHttpStatus422() {
        assertThat(AiAgentErrorCatalog.DEPRECATED_EVENT_CONFIG_CANNOT_BE_ACTIVATED.httpStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(AiAgentErrorCatalog.EVENT_CONFIG_EVENT_DEFINITION_NOT_ACTIVE.httpStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(AiAgentErrorCatalog.EVENT_CONFIG_AGENT_NOT_ACTIVE.httpStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(AiAgentErrorCatalog.EVENT_CONFIG_PROMPT_VERSION_NOT_ACTIVE.httpStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(AiAgentErrorCatalog.EVENT_CONFIG_PROMPT_TEMPLATE_NOT_ACTIVE.httpStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(AiAgentErrorCatalog.EVENT_CONFIG_PROMPT_TEMPLATE_AGENT_MISMATCH.httpStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(AiAgentErrorCatalog.EVENT_CONFIG_MODEL_DEPLOYMENT_NOT_ACTIVE.httpStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(AiAgentErrorCatalog.EVENT_CONFIG_MODEL_DEPLOYMENT_ENVIRONMENT_MISMATCH.httpStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void eventConfigInvalidInputEntries_haveHttpStatus400() {
        assertThat(AiAgentErrorCatalog.INVALID_EVENT_CONFIG_STATUS.httpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(AiAgentErrorCatalog.INVALID_EVENT_CONFIG_TRIGGER_TYPE.httpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(AiAgentErrorCatalog.INVALID_EVENT_CONFIG_ENVIRONMENT.httpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void usagePolicyNotFoundEntries_haveHttpStatus404() {
        assertThat(AiAgentErrorCatalog.USAGE_POLICY_NOT_FOUND.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(AiAgentErrorCatalog.USAGE_POLICY_EVENT_CONFIG_NOT_FOUND.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(AiAgentErrorCatalog.USAGE_POLICY_AGENT_NOT_FOUND.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(AiAgentErrorCatalog.USAGE_POLICY_MODEL_DEPLOYMENT_NOT_FOUND.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void usagePolicyConflictEntries_haveHttpStatus409() {
        assertThat(AiAgentErrorCatalog.USAGE_POLICY_CODE_ALREADY_EXISTS.httpStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(AiAgentErrorCatalog.USAGE_POLICY_ACTIVE_ALREADY_EXISTS.httpStatus()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void usagePolicyBusinessRuleEntries_haveHttpStatus422() {
        assertThat(AiAgentErrorCatalog.DEPRECATED_USAGE_POLICY_CANNOT_BE_ACTIVATED.httpStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(AiAgentErrorCatalog.USAGE_POLICY_NO_LIMIT_DEFINED.httpStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(AiAgentErrorCatalog.USAGE_POLICY_PERIOD_REQUIRED.httpStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void usagePolicyInvalidInputEntries_haveHttpStatus400() {
        assertThat(AiAgentErrorCatalog.INVALID_USAGE_POLICY_TARGET_TYPE.httpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(AiAgentErrorCatalog.INVALID_USAGE_POLICY_PERIOD.httpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(AiAgentErrorCatalog.INVALID_USAGE_POLICY_ACTION.httpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(AiAgentErrorCatalog.INVALID_USAGE_POLICY_STATUS.httpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(AiAgentErrorCatalog.USAGE_POLICY_TARGET_ID_REQUIRED.httpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(AiAgentErrorCatalog.USAGE_POLICY_TARGET_ID_MUST_BE_NULL.httpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void executionLogNotFoundEntries_haveHttpStatus404() {
        assertThat(AiAgentErrorCatalog.EXECUTION_LOG_NOT_FOUND.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(AiAgentErrorCatalog.EXECUTION_LOG_EVENT_CONFIG_NOT_FOUND.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(AiAgentErrorCatalog.EXECUTION_LOG_EVENT_DEFINITION_NOT_FOUND.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(AiAgentErrorCatalog.EXECUTION_LOG_AGENT_NOT_FOUND.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(AiAgentErrorCatalog.EXECUTION_LOG_PROMPT_VERSION_NOT_FOUND.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(AiAgentErrorCatalog.EXECUTION_LOG_MODEL_DEPLOYMENT_NOT_FOUND.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void executionLogConflictEntries_haveHttpStatus409() {
        assertThat(AiAgentErrorCatalog.EXECUTION_LOG_REQUEST_ID_ALREADY_EXISTS.httpStatus()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void executionLogBusinessRuleEntries_haveHttpStatus422() {
        assertThat(AiAgentErrorCatalog.INVALID_EXECUTION_STATUS_TRANSITION.httpStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(AiAgentErrorCatalog.TERMINAL_EXECUTION_LOG_CANNOT_BE_UPDATED.httpStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void executionLogInvalidInputEntries_haveHttpStatus400() {
        assertThat(AiAgentErrorCatalog.INVALID_EXECUTION_STATUS.httpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(AiAgentErrorCatalog.INVALID_EXECUTION_TRIGGER_SOURCE.httpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(AiAgentErrorCatalog.INVALID_EXECUTION_USAGE_METRICS.httpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(AiAgentErrorCatalog.INVALID_EXECUTION_METADATA_JSON.httpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void executionProviderIntegrationNotFoundEntries_haveHttpStatus404() {
        assertThat(AiAgentErrorCatalog.EXECUTION_EVENT_CONFIG_NOT_FOUND.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(AiAgentErrorCatalog.EXECUTION_EVENT_DEFINITION_NOT_FOUND.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(AiAgentErrorCatalog.EXECUTION_AGENT_NOT_FOUND.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(AiAgentErrorCatalog.EXECUTION_PROMPT_TEMPLATE_NOT_FOUND.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(AiAgentErrorCatalog.EXECUTION_PROMPT_VERSION_NOT_FOUND.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(AiAgentErrorCatalog.EXECUTION_MODEL_DEPLOYMENT_NOT_FOUND.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(AiAgentErrorCatalog.EXECUTION_AI_MODEL_NOT_FOUND.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(AiAgentErrorCatalog.EXECUTION_PROVIDER_NOT_FOUND.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void executionProviderIntegrationBusinessRuleEntries_haveHttpStatus422() {
        assertThat(AiAgentErrorCatalog.EXECUTION_EVENT_CONFIG_NOT_ACTIVE.httpStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(AiAgentErrorCatalog.EXECUTION_EVENT_DEFINITION_NOT_ACTIVE.httpStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(AiAgentErrorCatalog.EXECUTION_AGENT_NOT_ACTIVE.httpStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(AiAgentErrorCatalog.EXECUTION_PROMPT_TEMPLATE_NOT_ACTIVE.httpStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(AiAgentErrorCatalog.EXECUTION_PROMPT_VERSION_NOT_ACTIVE.httpStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(AiAgentErrorCatalog.EXECUTION_MODEL_DEPLOYMENT_NOT_ACTIVE.httpStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(AiAgentErrorCatalog.EXECUTION_MODEL_DEPLOYMENT_ENVIRONMENT_MISMATCH.httpStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(AiAgentErrorCatalog.EXECUTION_AI_MODEL_NOT_ACTIVE.httpStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(AiAgentErrorCatalog.EXECUTION_PROVIDER_NOT_ACTIVE.httpStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(AiAgentErrorCatalog.PROMPT_RENDER_FAILED.httpStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void openAiIntegrationEntries_haveCorrectHttpStatuses() {
        assertThat(AiAgentErrorCatalog.PROMPT_RENDER_VARIABLE_MISSING.httpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(AiAgentErrorCatalog.PROVIDER_ADAPTER_NOT_IMPLEMENTED.httpStatus()).isEqualTo(HttpStatus.NOT_IMPLEMENTED);
        assertThat(AiAgentErrorCatalog.OPENAI_API_KEY_MISSING.httpStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(AiAgentErrorCatalog.OPENAI_API_CALL_FAILED.httpStatus()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(AiAgentErrorCatalog.OPENAI_API_TIMEOUT.httpStatus()).isEqualTo(HttpStatus.GATEWAY_TIMEOUT);
        assertThat(AiAgentErrorCatalog.OPENAI_API_RESPONSE_INVALID.httpStatus()).isEqualTo(HttpStatus.BAD_GATEWAY);
    }
}

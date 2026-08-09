package com.company.scopery.modules.specpack.shared.error;

import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;

public enum SpecPackErrorCatalog implements ErrorCatalog {

    // SpecPack
    SPEC_PACK_NOT_FOUND("SPEC_PACK_NOT_FOUND", "Spec pack not found", HttpStatus.NOT_FOUND),
    SPEC_PACK_ARCHIVED("SPEC_PACK_ARCHIVED", "Spec pack is archived", HttpStatus.UNPROCESSABLE_ENTITY),
    SPEC_PACK_NOT_ARCHIVED("SPEC_PACK_NOT_ARCHIVED", "Spec pack is not archived", HttpStatus.UNPROCESSABLE_ENTITY),
    SPEC_PACK_PROJECT_MISMATCH("SPEC_PACK_PROJECT_MISMATCH", "Spec pack does not belong to this project", HttpStatus.UNPROCESSABLE_ENTITY),

    // Block
    BLOCK_NOT_FOUND("SPEC_PACK_BLOCK_NOT_FOUND", "Block not found", HttpStatus.NOT_FOUND),
    BLOCK_KEY_EXISTS("SPEC_PACK_BLOCK_KEY_EXISTS", "Block key already exists in this pack", HttpStatus.CONFLICT),
    BLOCK_KEY_INVALID("SPEC_PACK_BLOCK_KEY_INVALID", "Block key must not contain spaces", HttpStatus.BAD_REQUEST),
    BLOCK_STALE_REVISION("SPEC_PACK_BLOCK_STALE_REVISION", "Block was modified by another request; refresh and retry", HttpStatus.CONFLICT),
    BLOCK_REVISION_NOT_FOUND("SPEC_PACK_BLOCK_REVISION_NOT_FOUND", "Block revision not found", HttpStatus.NOT_FOUND),
    BLOCK_DELETED("SPEC_PACK_BLOCK_DELETED", "Block has been deleted", HttpStatus.UNPROCESSABLE_ENTITY),
    BLOCK_PARENT_NOT_FOUND("SPEC_PACK_BLOCK_PARENT_NOT_FOUND", "Parent block not found", HttpStatus.UNPROCESSABLE_ENTITY),
    BLOCK_PARENT_CYCLE("SPEC_PACK_BLOCK_PARENT_CYCLE", "Block parent reference creates a cycle", HttpStatus.UNPROCESSABLE_ENTITY),
    BLOCK_PAGE_BREAK_CANNOT_BE_PARENT("SPEC_PACK_BLOCK_PAGE_BREAK_CANNOT_BE_PARENT", "PAGE_BREAK block cannot be a parent", HttpStatus.BAD_REQUEST),

    // Import
    IMPORT_INVALID_SCHEMA_VERSION("SPEC_PACK_IMPORT_INVALID_SCHEMA", "Invalid import schema version", HttpStatus.BAD_REQUEST),
    IMPORT_TOO_MANY_ITEMS("SPEC_PACK_IMPORT_TOO_MANY_ITEMS", "Import payload exceeds maximum item count", HttpStatus.BAD_REQUEST),
    IMPORT_PAYLOAD_TOO_LARGE("SPEC_PACK_IMPORT_PAYLOAD_TOO_LARGE", "Import payload exceeds maximum size", HttpStatus.BAD_REQUEST),
    IMPORT_DUPLICATE_BLOCK_KEY("SPEC_PACK_IMPORT_DUPLICATE_BLOCK_KEY", "Duplicate block key in import payload", HttpStatus.BAD_REQUEST),
    IMPORT_INVALID_BLOCK("SPEC_PACK_IMPORT_INVALID_BLOCK", "Block validation failed", HttpStatus.BAD_REQUEST),

    // Version
    VERSION_NOT_FOUND("SPEC_PACK_VERSION_NOT_FOUND", "Pack version not found", HttpStatus.NOT_FOUND),
    VERSION_NUMBER_EXISTS("SPEC_PACK_VERSION_NUMBER_EXISTS", "Version number already exists for this pack", HttpStatus.CONFLICT),

    // Agent session
    AGENT_SESSION_NOT_FOUND("SPEC_PACK_AGENT_SESSION_NOT_FOUND", "Agent session not found", HttpStatus.NOT_FOUND),
    AGENT_SESSION_NOT_ACTIVE("SPEC_PACK_AGENT_SESSION_NOT_ACTIVE", "Agent session is not active", HttpStatus.UNPROCESSABLE_ENTITY),
    AGENT_SESSION_WRONG_STAGE("SPEC_PACK_AGENT_SESSION_WRONG_STAGE", "Agent session is not in the expected stage", HttpStatus.UNPROCESSABLE_ENTITY),
    AGENT_STAGE_NOT_FOUND("SPEC_PACK_AGENT_STAGE_NOT_FOUND", "Agent stage not found", HttpStatus.NOT_FOUND),
    AGENT_STAGE_INVALID_STATUS("SPEC_PACK_AGENT_STAGE_INVALID_STATUS", "Agent stage cannot be completed in its current status", HttpStatus.UNPROCESSABLE_ENTITY),
    AGENT_STAGE_ALREADY_IN_PROGRESS("SPEC_PACK_AGENT_STAGE_ALREADY_IN_PROGRESS", "Stage execution is already in progress", HttpStatus.CONFLICT),
    OUTLINE_NOT_APPROVED("SPEC_PACK_OUTLINE_NOT_APPROVED", "No approved outline found for this session", HttpStatus.UNPROCESSABLE_ENTITY),

    // Clarification
    CLARIFICATION_NOT_FOUND("SPEC_PACK_CLARIFICATION_NOT_FOUND", "Clarification not found", HttpStatus.NOT_FOUND),
    CLARIFICATION_CODE_EXISTS("SPEC_PACK_CLARIFICATION_CODE_EXISTS", "Clarification code already exists in this session", HttpStatus.CONFLICT),
    CLARIFICATION_NOT_OPEN("SPEC_PACK_CLARIFICATION_NOT_OPEN", "Clarification is not in OPEN status", HttpStatus.UNPROCESSABLE_ENTITY),

    // Readiness
    READINESS_BLOCKED("SPEC_PACK_READINESS_BLOCKED", "Session has open BLOCKING clarifications", HttpStatus.UNPROCESSABLE_ENTITY),

    // Outline
    OUTLINE_NOT_FOUND("SPEC_PACK_OUTLINE_NOT_FOUND", "Outline not found", HttpStatus.NOT_FOUND),
    OUTLINE_ALREADY_APPROVED("SPEC_PACK_OUTLINE_ALREADY_APPROVED", "Outline is already approved", HttpStatus.UNPROCESSABLE_ENTITY),
    OUTLINE_NOT_DRAFT("SPEC_PACK_OUTLINE_NOT_DRAFT", "Only draft outlines can be approved", HttpStatus.UNPROCESSABLE_ENTITY),

    // Prompt template
    PROMPT_TEMPLATE_NOT_FOUND("SPEC_PACK_PROMPT_TEMPLATE_NOT_FOUND", "Prompt template not found", HttpStatus.NOT_FOUND),
    PROMPT_TEMPLATE_NOT_PUBLISHED("SPEC_PACK_PROMPT_TEMPLATE_NOT_PUBLISHED", "Prompt template is not published", HttpStatus.UNPROCESSABLE_ENTITY),
    PROMPT_TEMPLATE_ALREADY_DEPRECATED("SPEC_PACK_PROMPT_TEMPLATE_ALREADY_DEPRECATED", "Prompt template is already deprecated", HttpStatus.UNPROCESSABLE_ENTITY),

    // Asset
    ASSET_NOT_FOUND("SPEC_PACK_ASSET_NOT_FOUND", "Asset not found", HttpStatus.NOT_FOUND),
    ASSET_REVISION_NOT_FOUND("SPEC_PACK_ASSET_REVISION_NOT_FOUND", "Asset revision not found", HttpStatus.NOT_FOUND),
    ASSET_INVALID_MIME("SPEC_PACK_ASSET_INVALID_MIME", "File MIME type is not allowed for this asset type", HttpStatus.BAD_REQUEST),
    ASSET_TOO_LARGE("SPEC_PACK_ASSET_TOO_LARGE", "Asset file exceeds maximum allowed size", HttpStatus.BAD_REQUEST),
    ASSET_ARCHIVED("SPEC_PACK_ASSET_ARCHIVED", "Asset is archived", HttpStatus.UNPROCESSABLE_ENTITY);

    private final String code;
    private final String defaultMessage;
    private final org.springframework.http.HttpStatus httpStatus;

    SpecPackErrorCatalog(String code, String defaultMessage, org.springframework.http.HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    @Override public String code() { return code; }
    @Override public String defaultMessage() { return defaultMessage; }
    @Override public org.springframework.http.HttpStatus httpStatus() { return httpStatus; }
}

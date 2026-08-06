package com.company.scopery.modules.specpack.shared.error;

import com.company.scopery.common.exception.AppException;

import java.util.Map;
import java.util.UUID;

public final class SpecPackExceptions {
    private SpecPackExceptions() {}

    // SpecPack
    public static AppException specPackNotFound(UUID id) {
        return new AppException(SpecPackErrorCatalog.SPEC_PACK_NOT_FOUND, "Spec pack not found: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException specPackArchived(UUID id) {
        return new AppException(SpecPackErrorCatalog.SPEC_PACK_ARCHIVED, "Spec pack is archived: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException specPackNotArchived(UUID id) {
        return new AppException(SpecPackErrorCatalog.SPEC_PACK_NOT_ARCHIVED, "Spec pack is not archived: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException specPackProjectMismatch(UUID packId, UUID projectId) {
        return new AppException(SpecPackErrorCatalog.SPEC_PACK_PROJECT_MISMATCH, "Pack " + packId + " does not belong to project " + projectId, Map.of("packId", packId == null ? "" : packId, "projectId", projectId == null ? "" : projectId));
    }

    // Block
    public static AppException blockNotFound(UUID id) {
        return new AppException(SpecPackErrorCatalog.BLOCK_NOT_FOUND, "Block not found: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException blockKeyExists(String blockKey) {
        return new AppException(SpecPackErrorCatalog.BLOCK_KEY_EXISTS, "Block key already exists: " + blockKey, Map.of("blockKey", blockKey));
    }
    public static AppException blockKeyInvalid(String blockKey) {
        return new AppException(SpecPackErrorCatalog.BLOCK_KEY_INVALID, "Block key must not contain spaces: " + blockKey, Map.of("blockKey", blockKey));
    }
    public static AppException blockStaleRevision(UUID id, int expected, int actual) {
        return new AppException(SpecPackErrorCatalog.BLOCK_STALE_REVISION, "Block " + id + " expected revision " + expected + " but found " + actual, Map.of("blockId", id == null ? "" : id, "expectedRevision", expected, "actualRevision", actual));
    }
    public static AppException blockRevisionNotFound(UUID blockId, int revisionNumber) {
        return new AppException(SpecPackErrorCatalog.BLOCK_REVISION_NOT_FOUND, "Revision " + revisionNumber + " not found for block " + blockId, Map.of("blockId", blockId == null ? "" : blockId, "revisionNumber", revisionNumber));
    }
    public static AppException blockDeleted(UUID id) {
        return new AppException(SpecPackErrorCatalog.BLOCK_DELETED, "Block has been deleted: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException blockParentNotFound(String parentBlockKey) {
        return new AppException(SpecPackErrorCatalog.BLOCK_PARENT_NOT_FOUND, "Parent block not found: " + parentBlockKey, Map.of("parentBlockKey", parentBlockKey));
    }
    public static AppException blockParentCycle(String blockKey) {
        return new AppException(SpecPackErrorCatalog.BLOCK_PARENT_CYCLE, "Cycle detected for block: " + blockKey, Map.of("blockKey", blockKey));
    }
    public static AppException blockPageBreakCannotBeParent() {
        return new AppException(SpecPackErrorCatalog.BLOCK_PAGE_BREAK_CANNOT_BE_PARENT);
    }

    // Import
    public static AppException importInvalidSchemaVersion(String version) {
        return new AppException(SpecPackErrorCatalog.IMPORT_INVALID_SCHEMA_VERSION, "Invalid schema version: " + version, Map.of("schemaVersion", version == null ? "" : version));
    }
    public static AppException importTooManyItems(int count, int max) {
        return new AppException(SpecPackErrorCatalog.IMPORT_TOO_MANY_ITEMS, "Import has " + count + " items; max is " + max, Map.of("count", count, "max", max));
    }
    public static AppException importPayloadTooLarge(long bytes, long maxBytes) {
        return new AppException(SpecPackErrorCatalog.IMPORT_PAYLOAD_TOO_LARGE, "Payload is " + bytes + " bytes; max is " + maxBytes, Map.of("bytes", bytes, "maxBytes", maxBytes));
    }
    public static AppException importDuplicateBlockKey(String blockKey) {
        return new AppException(SpecPackErrorCatalog.IMPORT_DUPLICATE_BLOCK_KEY, "Duplicate block key in payload: " + blockKey, Map.of("blockKey", blockKey));
    }
    public static AppException importInvalidBlock(String blockKey, String reason) {
        return new AppException(SpecPackErrorCatalog.IMPORT_INVALID_BLOCK, reason, Map.of("blockKey", blockKey == null ? "" : blockKey));
    }

    // Version
    public static AppException versionNotFound(UUID id) {
        return new AppException(SpecPackErrorCatalog.VERSION_NOT_FOUND, "Pack version not found: " + id, Map.of("id", id == null ? "" : id));
    }

    // Agent session
    public static AppException agentSessionNotFound(UUID id) {
        return new AppException(SpecPackErrorCatalog.AGENT_SESSION_NOT_FOUND, "Agent session not found: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException agentSessionNotActive(UUID id) {
        return new AppException(SpecPackErrorCatalog.AGENT_SESSION_NOT_ACTIVE, "Agent session is not active: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException agentStageNotFound(UUID sessionId, String stageCode) {
        return new AppException(SpecPackErrorCatalog.AGENT_STAGE_NOT_FOUND, "Stage " + stageCode + " not found in session " + sessionId, Map.of("sessionId", sessionId == null ? "" : sessionId, "stageCode", stageCode == null ? "" : stageCode));
    }
    public static AppException agentStageInvalidStatus(String stageCode, String status) {
        return new AppException(SpecPackErrorCatalog.AGENT_STAGE_INVALID_STATUS, "Stage " + stageCode + " cannot be completed from status " + status, Map.of("stageCode", stageCode, "status", status));
    }

    // Clarification
    public static AppException clarificationNotFound(UUID id) {
        return new AppException(SpecPackErrorCatalog.CLARIFICATION_NOT_FOUND, "Clarification not found: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException clarificationCodeExists(String code) {
        return new AppException(SpecPackErrorCatalog.CLARIFICATION_CODE_EXISTS, "Clarification code already exists: " + code, Map.of("code", code));
    }
    public static AppException clarificationNotOpen(UUID id) {
        return new AppException(SpecPackErrorCatalog.CLARIFICATION_NOT_OPEN, "Clarification is not open: " + id, Map.of("id", id == null ? "" : id));
    }

    // Outline
    public static AppException outlineNotFound(UUID id) {
        return new AppException(SpecPackErrorCatalog.OUTLINE_NOT_FOUND, "Outline not found: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException outlineAlreadyApproved(UUID id) {
        return new AppException(SpecPackErrorCatalog.OUTLINE_ALREADY_APPROVED, "Outline is already approved: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException outlineNotDraft(UUID id) {
        return new AppException(SpecPackErrorCatalog.OUTLINE_NOT_DRAFT, "Only draft outlines can be approved: " + id, Map.of("id", id == null ? "" : id));
    }

    // Prompt template
    public static AppException promptTemplateNotFound(UUID id) {
        return new AppException(SpecPackErrorCatalog.PROMPT_TEMPLATE_NOT_FOUND, "Prompt template not found: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException promptTemplateNotPublished(UUID id) {
        return new AppException(SpecPackErrorCatalog.PROMPT_TEMPLATE_NOT_PUBLISHED, "Prompt template is not published: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException promptTemplateAlreadyDeprecated(UUID id) {
        return new AppException(SpecPackErrorCatalog.PROMPT_TEMPLATE_ALREADY_DEPRECATED, "Prompt template is already deprecated: " + id, Map.of("id", id == null ? "" : id));
    }

    // Asset
    public static AppException assetNotFound(UUID id) {
        return new AppException(SpecPackErrorCatalog.ASSET_NOT_FOUND, "Asset not found: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException assetInvalidMime(String mimeType) {
        return new AppException(SpecPackErrorCatalog.ASSET_INVALID_MIME, "MIME type not allowed: " + mimeType, Map.of("mimeType", mimeType == null ? "" : mimeType));
    }
    public static AppException assetTooLarge(long bytes, long maxBytes) {
        return new AppException(SpecPackErrorCatalog.ASSET_TOO_LARGE, "Asset is " + bytes + " bytes; max is " + maxBytes, Map.of("bytes", bytes, "maxBytes", maxBytes));
    }
    public static AppException assetArchived(UUID id) {
        return new AppException(SpecPackErrorCatalog.ASSET_ARCHIVED, "Asset is archived: " + id, Map.of("id", id == null ? "" : id));
    }
}

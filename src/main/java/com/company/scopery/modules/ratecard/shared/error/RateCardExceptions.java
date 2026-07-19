package com.company.scopery.modules.ratecard.shared.error;

import com.company.scopery.common.exception.AppException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public final class RateCardExceptions {

    private RateCardExceptions() {}

    public static AppException costRoleNotFound(UUID id) {
        return new AppException(RateCardErrorCatalog.COST_ROLE_NOT_FOUND,
                "Cost role not found: " + id, Map.of("id", id));
    }

    public static AppException costRoleCodeAlreadyExists(String code) {
        return new AppException(RateCardErrorCatalog.COST_ROLE_CODE_ALREADY_EXISTS,
                "Cost role code already exists: " + code, Map.of("code", code));
    }

    public static AppException costRoleInvalidScope(String scope) {
        return new AppException(RateCardErrorCatalog.COST_ROLE_INVALID_SCOPE,
                "Invalid cost role scope: " + scope, Map.of("scope", scope));
    }

    public static AppException costRoleArchived(UUID id) {
        return new AppException(RateCardErrorCatalog.COST_ROLE_ARCHIVED,
                "Cost role is archived: " + id, Map.of("id", id));
    }

    public static AppException costRoleInUse(UUID id) {
        return new AppException(RateCardErrorCatalog.COST_ROLE_IN_USE,
                "Cost role is in use: " + id, Map.of("id", id));
    }

    public static AppException costRoleBuiltInCannotDelete(UUID id) {
        return new AppException(RateCardErrorCatalog.COST_ROLE_BUILT_IN_CANNOT_DELETE,
                "Built-in cost role cannot be hard-deleted: " + id, Map.of("id", id));
    }

    public static AppException costRoleNotActive(UUID id) {
        return new AppException(RateCardErrorCatalog.COST_ROLE_NOT_ACTIVE,
                "Cost role is not active: " + id, Map.of("id", id));
    }

    public static AppException memberAssignmentNotFound(UUID id) {
        return new AppException(RateCardErrorCatalog.MEMBER_COST_ROLE_ASSIGNMENT_NOT_FOUND,
                "Member cost role assignment not found: " + id, Map.of("id", id));
    }

    public static AppException memberInactive(UUID memberId) {
        return new AppException(RateCardErrorCatalog.MEMBER_COST_ROLE_MEMBER_INACTIVE,
                "Workspace member is inactive: " + memberId, Map.of("workspaceMemberId", memberId));
    }

    public static AppException memberNotFound(UUID memberId) {
        return new AppException(RateCardErrorCatalog.MEMBER_COST_ROLE_MEMBER_NOT_FOUND,
                "Workspace member not found: " + memberId, Map.of("workspaceMemberId", memberId));
    }

    public static AppException memberRoleInactive(UUID roleId) {
        return new AppException(RateCardErrorCatalog.MEMBER_COST_ROLE_ROLE_INACTIVE,
                "Cost role is inactive: " + roleId, Map.of("costRoleId", roleId));
    }

    public static AppException memberDateRangeInvalid() {
        return new AppException(RateCardErrorCatalog.MEMBER_COST_ROLE_DATE_RANGE_INVALID);
    }

    public static AppException memberOverlap(UUID memberId) {
        return new AppException(RateCardErrorCatalog.MEMBER_COST_ROLE_OVERLAP,
                "Overlapping default cost role for member: " + memberId,
                Map.of("workspaceMemberId", memberId));
    }

    public static AppException rateCardNotFound(UUID id) {
        return new AppException(RateCardErrorCatalog.RATE_CARD_NOT_FOUND,
                "Rate card not found: " + id, Map.of("id", id));
    }

    public static AppException rateCardCodeAlreadyExists(String code) {
        return new AppException(RateCardErrorCatalog.RATE_CARD_CODE_ALREADY_EXISTS,
                "Rate card code already exists: " + code, Map.of("code", code));
    }

    public static AppException rateCardInvalidScope(String scope) {
        return new AppException(RateCardErrorCatalog.RATE_CARD_INVALID_SCOPE,
                "Invalid rate card scope: " + scope, Map.of("scope", scope));
    }

    public static AppException rateCardArchived(UUID id) {
        return new AppException(RateCardErrorCatalog.RATE_CARD_ARCHIVED,
                "Rate card is archived: " + id, Map.of("id", id));
    }

    public static AppException rateCardNoPublishedVersion(UUID id) {
        return new AppException(RateCardErrorCatalog.RATE_CARD_NO_PUBLISHED_VERSION,
                "Rate card has no published version: " + id, Map.of("id", id));
    }

    public static AppException workspaceNotFound(UUID workspaceId) {
        return new AppException(RateCardErrorCatalog.RATE_CARD_WORKSPACE_NOT_FOUND,
                "Workspace not found: " + workspaceId, Map.of("workspaceId", workspaceId));
    }

    public static AppException workspaceNotActive(UUID workspaceId) {
        return new AppException(RateCardErrorCatalog.RATE_CARD_WORKSPACE_NOT_ACTIVE,
                "Workspace is not active: " + workspaceId, Map.of("workspaceId", workspaceId));
    }

    public static AppException organizationNotFound(UUID organizationId) {
        return new AppException(RateCardErrorCatalog.RATE_CARD_ORGANIZATION_NOT_FOUND,
                "Organization not found: " + organizationId, Map.of("organizationId", organizationId));
    }

    public static AppException organizationNotActive(UUID organizationId) {
        return new AppException(RateCardErrorCatalog.RATE_CARD_ORGANIZATION_NOT_ACTIVE,
                "Organization is not active: " + organizationId, Map.of("organizationId", organizationId));
    }

    public static AppException versionNotFound(UUID id) {
        return new AppException(RateCardErrorCatalog.RATE_CARD_VERSION_NOT_FOUND,
                "Rate card version not found: " + id, Map.of("id", id));
    }

    public static AppException versionNotDraft(UUID id) {
        return new AppException(RateCardErrorCatalog.RATE_CARD_VERSION_NOT_DRAFT,
                "Rate card version is not DRAFT: " + id, Map.of("id", id));
    }

    public static AppException versionNotPublished(UUID id) {
        return new AppException(RateCardErrorCatalog.RATE_CARD_VERSION_NOT_PUBLISHED,
                "Rate card version is not PUBLISHED: " + id, Map.of("id", id));
    }

    public static AppException versionAlreadyPublished(UUID id) {
        return new AppException(RateCardErrorCatalog.RATE_CARD_VERSION_ALREADY_PUBLISHED,
                "Rate card version already published: " + id, Map.of("id", id));
    }

    public static AppException versionDateRangeInvalid() {
        return new AppException(RateCardErrorCatalog.RATE_CARD_VERSION_DATE_RANGE_INVALID);
    }

    public static AppException versionOverlap(UUID rateCardId) {
        return new AppException(RateCardErrorCatalog.RATE_CARD_VERSION_OVERLAP,
                "Published version date range overlaps for rate card: " + rateCardId,
                Map.of("rateCardId", rateCardId));
    }

    public static AppException versionNoLines(UUID id) {
        return new AppException(RateCardErrorCatalog.RATE_CARD_VERSION_NO_LINES,
                "Rate card version has no lines: " + id, Map.of("id", id));
    }

    public static AppException versionStructureInvalid(String reason) {
        return new AppException(RateCardErrorCatalog.RATE_CARD_VERSION_STRUCTURE_INVALID,
                "Rate card version structure invalid: " + reason, Map.of("reason", reason));
    }

    public static AppException lineNotFound(UUID id) {
        return new AppException(RateCardErrorCatalog.RATE_CARD_LINE_NOT_FOUND,
                "Rate card line not found: " + id, Map.of("id", id));
    }

    public static AppException lineDuplicate() {
        return new AppException(RateCardErrorCatalog.RATE_CARD_LINE_DUPLICATE);
    }

    public static AppException invalidCostRate(BigDecimal rate) {
        return new AppException(RateCardErrorCatalog.RATE_CARD_LINE_INVALID_COST_RATE,
                "Invalid cost rate: " + rate, Map.of("costRatePerHour", rate));
    }

    public static AppException invalidBillingRate(BigDecimal rate) {
        return new AppException(RateCardErrorCatalog.RATE_CARD_LINE_INVALID_BILLING_RATE,
                "Invalid billing rate: " + rate, Map.of("billingRatePerHour", rate));
    }

    public static AppException invalidCurrency(String currencyCode) {
        return new AppException(RateCardErrorCatalog.RATE_CARD_LINE_INVALID_CURRENCY,
                "Invalid currency: " + currencyCode,
                Map.of("currencyCode", currencyCode == null ? "" : currencyCode));
    }

    public static AppException lineRoleInactive(UUID roleId) {
        return new AppException(RateCardErrorCatalog.RATE_CARD_LINE_ROLE_INACTIVE,
                "Cost role inactive for line: " + roleId, Map.of("costRoleId", roleId));
    }

    public static AppException inflationNotFound(UUID id) {
        return new AppException(RateCardErrorCatalog.INFLATION_POLICY_NOT_FOUND,
                "Inflation policy not found: " + id, Map.of("id", id));
    }

    public static AppException inflationCodeAlreadyExists(String code) {
        return new AppException(RateCardErrorCatalog.INFLATION_POLICY_CODE_ALREADY_EXISTS,
                "Inflation policy code already exists: " + code, Map.of("code", code));
    }

    public static AppException inflationInvalidPercent(BigDecimal percent) {
        return new AppException(RateCardErrorCatalog.INFLATION_POLICY_INVALID_PERCENT,
                "Invalid inflation percent: " + percent, Map.of("inflationPercent", percent));
    }

    public static AppException inflationDateRangeInvalid() {
        return new AppException(RateCardErrorCatalog.INFLATION_POLICY_DATE_RANGE_INVALID);
    }

    public static AppException inflationInvalidScope(String scope) {
        return new AppException(RateCardErrorCatalog.INFLATION_POLICY_INVALID_SCOPE,
                "Invalid inflation policy scope: " + scope, Map.of("scope", scope));
    }

    public static AppException inflationArchived(UUID id) {
        return new AppException(RateCardErrorCatalog.INFLATION_POLICY_ARCHIVED,
                "Inflation policy is archived: " + id, Map.of("id", id));
    }

    public static AppException resolutionRoleNotFound(String roleRef) {
        return new AppException(RateCardErrorCatalog.RATE_RESOLUTION_ROLE_NOT_FOUND,
                "Cost role not found for resolution: " + roleRef, Map.of("costRole", roleRef));
    }

    public static AppException noApplicableCard() {
        return new AppException(RateCardErrorCatalog.RATE_RESOLUTION_NO_APPLICABLE_CARD);
    }

    public static AppException noApplicableVersion() {
        return new AppException(RateCardErrorCatalog.RATE_RESOLUTION_NO_APPLICABLE_VERSION);
    }

    public static AppException noApplicableLine() {
        return new AppException(RateCardErrorCatalog.RATE_RESOLUTION_NO_APPLICABLE_LINE);
    }

    public static AppException resolutionAccessDenied() {
        return new AppException(RateCardErrorCatalog.RATE_RESOLUTION_ACCESS_DENIED);
    }

    public static AppException accessDenied() {
        return new AppException(RateCardErrorCatalog.RATE_CARD_ACCESS_DENIED);
    }

    public static AppException roleNotResolved() {
        return new AppException(RateCardErrorCatalog.RATE_ROLE_NOT_RESOLVED);
    }
}

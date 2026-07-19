package com.company.scopery.modules.ratecard.shared.error;

import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;

public enum RateCardErrorCatalog implements ErrorCatalog {

    COST_ROLE_NOT_FOUND(
            "COST_ROLE_NOT_FOUND", "Cost role not found", HttpStatus.NOT_FOUND),
    COST_ROLE_CODE_ALREADY_EXISTS(
            "COST_ROLE_CODE_ALREADY_EXISTS", "Cost role code already exists in this scope", HttpStatus.CONFLICT),
    COST_ROLE_INVALID_SCOPE(
            "COST_ROLE_INVALID_SCOPE", "Cost role scope is invalid", HttpStatus.BAD_REQUEST),
    COST_ROLE_ARCHIVED(
            "COST_ROLE_ARCHIVED", "Cost role is archived", HttpStatus.UNPROCESSABLE_ENTITY),
    COST_ROLE_IN_USE(
            "COST_ROLE_IN_USE", "Cost role is in use and cannot be archived", HttpStatus.UNPROCESSABLE_ENTITY),
    COST_ROLE_BUILT_IN_CANNOT_DELETE(
            "COST_ROLE_BUILT_IN_CANNOT_DELETE", "Built-in cost role cannot be hard-deleted", HttpStatus.UNPROCESSABLE_ENTITY),
    COST_ROLE_NOT_ACTIVE(
            "COST_ROLE_NOT_ACTIVE", "Cost role must be ACTIVE", HttpStatus.UNPROCESSABLE_ENTITY),
    MEMBER_COST_ROLE_ASSIGNMENT_NOT_FOUND(
            "MEMBER_COST_ROLE_ASSIGNMENT_NOT_FOUND", "Member cost role assignment not found", HttpStatus.NOT_FOUND),
    MEMBER_COST_ROLE_MEMBER_INACTIVE(
            "MEMBER_COST_ROLE_MEMBER_INACTIVE", "Workspace member is inactive", HttpStatus.UNPROCESSABLE_ENTITY),
    MEMBER_COST_ROLE_ROLE_INACTIVE(
            "MEMBER_COST_ROLE_ROLE_INACTIVE", "Cost role is inactive for assignment", HttpStatus.UNPROCESSABLE_ENTITY),
    MEMBER_COST_ROLE_DATE_RANGE_INVALID(
            "MEMBER_COST_ROLE_DATE_RANGE_INVALID", "Member cost role date range is invalid", HttpStatus.BAD_REQUEST),
    MEMBER_COST_ROLE_OVERLAP(
            "MEMBER_COST_ROLE_OVERLAP", "Overlapping default member cost role assignment", HttpStatus.CONFLICT),
    MEMBER_COST_ROLE_MEMBER_NOT_FOUND(
            "MEMBER_COST_ROLE_MEMBER_NOT_FOUND", "Workspace member not found", HttpStatus.NOT_FOUND),
    RATE_CARD_NOT_FOUND(
            "RATE_CARD_NOT_FOUND", "Rate card not found", HttpStatus.NOT_FOUND),
    RATE_CARD_CODE_ALREADY_EXISTS(
            "RATE_CARD_CODE_ALREADY_EXISTS", "Rate card code already exists in this scope", HttpStatus.CONFLICT),
    RATE_CARD_INVALID_SCOPE(
            "RATE_CARD_INVALID_SCOPE", "Rate card scope is invalid", HttpStatus.BAD_REQUEST),
    RATE_CARD_ARCHIVED(
            "RATE_CARD_ARCHIVED", "Rate card is archived", HttpStatus.UNPROCESSABLE_ENTITY),
    RATE_CARD_NO_PUBLISHED_VERSION(
            "RATE_CARD_NO_PUBLISHED_VERSION", "Rate card has no published version", HttpStatus.UNPROCESSABLE_ENTITY),
    RATE_CARD_WORKSPACE_NOT_FOUND(
            "RATE_CARD_WORKSPACE_NOT_FOUND", "Workspace not found for rate card", HttpStatus.NOT_FOUND),
    RATE_CARD_WORKSPACE_NOT_ACTIVE(
            "RATE_CARD_WORKSPACE_NOT_ACTIVE", "Workspace must be ACTIVE for rate card", HttpStatus.UNPROCESSABLE_ENTITY),
    RATE_CARD_ORGANIZATION_NOT_FOUND(
            "RATE_CARD_ORGANIZATION_NOT_FOUND", "Organization not found for rate card", HttpStatus.NOT_FOUND),
    RATE_CARD_ORGANIZATION_NOT_ACTIVE(
            "RATE_CARD_ORGANIZATION_NOT_ACTIVE", "Organization must be ACTIVE for rate card", HttpStatus.UNPROCESSABLE_ENTITY),
    RATE_CARD_VERSION_NOT_FOUND(
            "RATE_CARD_VERSION_NOT_FOUND", "Rate card version not found", HttpStatus.NOT_FOUND),
    RATE_CARD_VERSION_NOT_DRAFT(
            "RATE_CARD_VERSION_NOT_DRAFT", "Rate card version must be DRAFT", HttpStatus.UNPROCESSABLE_ENTITY),
    RATE_CARD_VERSION_NOT_PUBLISHED(
            "RATE_CARD_VERSION_NOT_PUBLISHED", "Rate card version must be PUBLISHED", HttpStatus.UNPROCESSABLE_ENTITY),
    RATE_CARD_VERSION_ALREADY_PUBLISHED(
            "RATE_CARD_VERSION_ALREADY_PUBLISHED", "Rate card version is already published", HttpStatus.UNPROCESSABLE_ENTITY),
    RATE_CARD_VERSION_DATE_RANGE_INVALID(
            "RATE_CARD_VERSION_DATE_RANGE_INVALID", "Rate card version date range is invalid", HttpStatus.BAD_REQUEST),
    RATE_CARD_VERSION_OVERLAP(
            "RATE_CARD_VERSION_OVERLAP", "Published rate card version date range overlaps another published version", HttpStatus.CONFLICT),
    RATE_CARD_VERSION_NO_LINES(
            "RATE_CARD_VERSION_NO_LINES", "Rate card version must have at least one line to publish", HttpStatus.UNPROCESSABLE_ENTITY),
    RATE_CARD_VERSION_STRUCTURE_INVALID(
            "RATE_CARD_VERSION_STRUCTURE_INVALID", "Rate card version structure is invalid", HttpStatus.UNPROCESSABLE_ENTITY),
    RATE_CARD_LINE_NOT_FOUND(
            "RATE_CARD_LINE_NOT_FOUND", "Rate card line not found", HttpStatus.NOT_FOUND),
    RATE_CARD_LINE_DUPLICATE(
            "RATE_CARD_LINE_DUPLICATE", "Duplicate rate card line for role/seniority/location/currency", HttpStatus.CONFLICT),
    RATE_CARD_LINE_INVALID_COST_RATE(
            "RATE_CARD_LINE_INVALID_COST_RATE", "Cost rate per hour must be greater than zero", HttpStatus.BAD_REQUEST),
    RATE_CARD_LINE_INVALID_BILLING_RATE(
            "RATE_CARD_LINE_INVALID_BILLING_RATE", "Billing rate per hour must be null or greater than zero", HttpStatus.BAD_REQUEST),
    RATE_CARD_LINE_INVALID_CURRENCY(
            "RATE_CARD_LINE_INVALID_CURRENCY", "Currency code is invalid or unsupported", HttpStatus.BAD_REQUEST),
    RATE_CARD_LINE_ROLE_INACTIVE(
            "RATE_CARD_LINE_ROLE_INACTIVE", "Cost role must be ACTIVE for rate card line", HttpStatus.UNPROCESSABLE_ENTITY),
    INFLATION_POLICY_NOT_FOUND(
            "INFLATION_POLICY_NOT_FOUND", "Inflation policy not found", HttpStatus.NOT_FOUND),
    INFLATION_POLICY_CODE_ALREADY_EXISTS(
            "INFLATION_POLICY_CODE_ALREADY_EXISTS", "Inflation policy code already exists in this scope", HttpStatus.CONFLICT),
    INFLATION_POLICY_INVALID_PERCENT(
            "INFLATION_POLICY_INVALID_PERCENT", "Inflation percent must be greater than or equal to zero", HttpStatus.BAD_REQUEST),
    INFLATION_POLICY_DATE_RANGE_INVALID(
            "INFLATION_POLICY_DATE_RANGE_INVALID", "Inflation policy date range is invalid", HttpStatus.BAD_REQUEST),
    INFLATION_POLICY_INVALID_SCOPE(
            "INFLATION_POLICY_INVALID_SCOPE", "Inflation policy scope is invalid", HttpStatus.BAD_REQUEST),
    INFLATION_POLICY_ARCHIVED(
            "INFLATION_POLICY_ARCHIVED", "Inflation policy is archived", HttpStatus.UNPROCESSABLE_ENTITY),
    RATE_RESOLUTION_NOT_FOUND(
            "RATE_RESOLUTION_NOT_FOUND", "Rate resolution failed", HttpStatus.NOT_FOUND),
    RATE_RESOLUTION_ROLE_NOT_FOUND(
            "RATE_RESOLUTION_ROLE_NOT_FOUND", "Cost role not found for rate resolution", HttpStatus.NOT_FOUND),
    RATE_RESOLUTION_NO_APPLICABLE_CARD(
            "RATE_RESOLUTION_NO_APPLICABLE_CARD", "No applicable rate card found", HttpStatus.UNPROCESSABLE_ENTITY),
    RATE_RESOLUTION_NO_APPLICABLE_VERSION(
            "RATE_RESOLUTION_NO_APPLICABLE_VERSION", "No applicable published rate card version found", HttpStatus.UNPROCESSABLE_ENTITY),
    RATE_RESOLUTION_NO_APPLICABLE_LINE(
            "RATE_RESOLUTION_NO_APPLICABLE_LINE", "No applicable rate card line found", HttpStatus.UNPROCESSABLE_ENTITY),
    RATE_RESOLUTION_ACCESS_DENIED(
            "RATE_RESOLUTION_ACCESS_DENIED", "Rate resolution access is denied", HttpStatus.FORBIDDEN),
    RATE_CARD_ACCESS_DENIED(
            "RATE_CARD_ACCESS_DENIED", "Rate card access is denied", HttpStatus.FORBIDDEN),
    RATE_ROLE_NOT_RESOLVED(
            "RATE_ROLE_NOT_RESOLVED", "Cost role could not be resolved for the task", HttpStatus.UNPROCESSABLE_ENTITY);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    RateCardErrorCatalog(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String defaultMessage() {
        return defaultMessage;
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }
}

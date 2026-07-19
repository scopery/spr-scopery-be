package com.company.scopery.modules.servicesupport.shared.error;
import com.company.scopery.common.exception.ErrorCatalog; import org.springframework.http.HttpStatus;
public enum SupportErrorCatalog implements ErrorCatalog {
    SUPPORT_ACCESS_DENIED("SUPPORT_ACCESS_DENIED","Support access denied",HttpStatus.FORBIDDEN),
    SUPPORT_CASE_NOT_FOUND("SUPPORT_CASE_NOT_FOUND","Support case not found",HttpStatus.NOT_FOUND),
    SUPPORT_CASE_INVALID_STATUS("SUPPORT_CASE_INVALID_STATUS","Invalid support case status",HttpStatus.UNPROCESSABLE_ENTITY),
    SUPPORT_COMMENT_INTERNAL_NOT_PORTAL_VISIBLE("SUPPORT_COMMENT_INTERNAL_HIDDEN","Internal comment not portal visible",HttpStatus.FORBIDDEN),
    WARRANTY_NOT_FOUND("WARRANTY_NOT_FOUND","Warranty coverage not found",HttpStatus.NOT_FOUND),
    SUPPORT_EFFORT_NOT_FOUND("SUPPORT_EFFORT_NOT_FOUND","Support effort record not found",HttpStatus.NOT_FOUND),
    SERVICE_PROFILE_NOT_FOUND("SERVICE_PROFILE_NOT_FOUND","Service profile not found",HttpStatus.NOT_FOUND),
    SUPPORT_QUEUE_NOT_FOUND("SUPPORT_QUEUE_NOT_FOUND","Support queue not found",HttpStatus.NOT_FOUND),
    SLA_POLICY_NOT_FOUND("SLA_POLICY_NOT_FOUND","SLA policy not found",HttpStatus.NOT_FOUND),
    SLA_TARGET_NOT_FOUND("SLA_TARGET_NOT_FOUND","SLA target not found",HttpStatus.NOT_FOUND),
    INCIDENT_NOT_FOUND("INCIDENT_NOT_FOUND","Incident record not found",HttpStatus.NOT_FOUND),
    INCIDENT_INVALID_STATUS("INCIDENT_INVALID_STATUS","Invalid incident status transition",HttpStatus.UNPROCESSABLE_ENTITY),
    PROBLEM_NOT_FOUND("PROBLEM_NOT_FOUND","Problem record not found",HttpStatus.NOT_FOUND),
    MAINTENANCE_PLAN_NOT_FOUND("MAINTENANCE_PLAN_NOT_FOUND","Maintenance plan not found",HttpStatus.NOT_FOUND),
    MAINTENANCE_WINDOW_NOT_FOUND("MAINTENANCE_WINDOW_NOT_FOUND","Maintenance window not found",HttpStatus.NOT_FOUND),
    MAINTENANCE_ACTIVITY_NOT_FOUND("MAINTENANCE_ACTIVITY_NOT_FOUND","Maintenance activity not found",HttpStatus.NOT_FOUND),
    HANDOVER_PACKAGE_NOT_FOUND("HANDOVER_PACKAGE_NOT_FOUND","Handover package not found",HttpStatus.NOT_FOUND),
    HANDOVER_ITEM_NOT_FOUND("HANDOVER_ITEM_NOT_FOUND","Handover package item not found",HttpStatus.NOT_FOUND),
    WORK_LINK_NOT_FOUND("WORK_LINK_NOT_FOUND","Work link not found",HttpStatus.NOT_FOUND),
    COST_INPUT_NOT_FOUND("COST_INPUT_NOT_FOUND","Service cost input not found",HttpStatus.NOT_FOUND),
    ESCALATION_RULE_NOT_FOUND("ESCALATION_RULE_NOT_FOUND","Escalation rule not found",HttpStatus.NOT_FOUND),
    ESCALATION_RULE_CODE_EXISTS("ESCALATION_RULE_CODE_EXISTS","Escalation rule code already exists",HttpStatus.CONFLICT),
    KNOWLEDGE_LINK_NOT_FOUND("KNOWLEDGE_LINK_NOT_FOUND","Knowledge link not found",HttpStatus.NOT_FOUND),
    REQUEST_TYPE_NOT_FOUND("REQUEST_TYPE_NOT_FOUND","Support request type not found",HttpStatus.NOT_FOUND),
    REQUEST_TYPE_CODE_EXISTS("REQUEST_TYPE_CODE_EXISTS","Request type code already exists",HttpStatus.CONFLICT);
    private final String code; private final String defaultMessage; private final HttpStatus httpStatus;
    SupportErrorCatalog(String c,String m,HttpStatus s){code=c;defaultMessage=m;httpStatus=s;}
    @Override public String code(){return code;} @Override public String defaultMessage(){return defaultMessage;} @Override public HttpStatus httpStatus(){return httpStatus;}
}

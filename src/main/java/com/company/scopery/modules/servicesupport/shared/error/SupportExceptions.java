package com.company.scopery.modules.servicesupport.shared.error;
import com.company.scopery.common.exception.AppException;
import java.util.Map; import java.util.UUID;
public final class SupportExceptions {
    private SupportExceptions(){}
    public static AppException accessDenied(){ return new AppException(SupportErrorCatalog.SUPPORT_ACCESS_DENIED); }
    public static AppException caseNotFound(UUID id){ return new AppException(SupportErrorCatalog.SUPPORT_CASE_NOT_FOUND,"Case not found: "+id, Map.of("id",id)); }
    public static AppException invalidStatus(){ return new AppException(SupportErrorCatalog.SUPPORT_CASE_INVALID_STATUS); }
    public static AppException warrantyNotFound(UUID id){ return new AppException(SupportErrorCatalog.WARRANTY_NOT_FOUND,"Warranty not found: "+id, Map.of("id",id)); }
    public static AppException effortNotFound(UUID id){ return new AppException(SupportErrorCatalog.SUPPORT_EFFORT_NOT_FOUND,"Effort not found: "+id, Map.of("id",id)); }
    public static AppException serviceProfileNotFound(UUID id){ return new AppException(SupportErrorCatalog.SERVICE_PROFILE_NOT_FOUND,"Service profile not found: "+id, Map.of("id",id)); }
    public static AppException queueNotFound(UUID id){ return new AppException(SupportErrorCatalog.SUPPORT_QUEUE_NOT_FOUND,"Queue not found: "+id, Map.of("id",id)); }
    public static AppException slaPolicyNotFound(UUID id){ return new AppException(SupportErrorCatalog.SLA_POLICY_NOT_FOUND,"SLA policy not found: "+id, Map.of("id",id)); }
    public static AppException slaTargetNotFound(UUID id){ return new AppException(SupportErrorCatalog.SLA_TARGET_NOT_FOUND,"SLA target not found: "+id, Map.of("id",id)); }
    public static AppException incidentNotFound(UUID id){ return new AppException(SupportErrorCatalog.INCIDENT_NOT_FOUND,"Incident not found: "+id, Map.of("id",id)); }
    public static AppException incidentInvalidStatus(){ return new AppException(SupportErrorCatalog.INCIDENT_INVALID_STATUS); }
    public static AppException problemNotFound(UUID id){ return new AppException(SupportErrorCatalog.PROBLEM_NOT_FOUND,"Problem not found: "+id, Map.of("id",id)); }
    public static AppException maintenancePlanNotFound(UUID id){ return new AppException(SupportErrorCatalog.MAINTENANCE_PLAN_NOT_FOUND,"Maintenance plan not found: "+id, Map.of("id",id)); }
    public static AppException maintenanceWindowNotFound(UUID id){ return new AppException(SupportErrorCatalog.MAINTENANCE_WINDOW_NOT_FOUND,"Maintenance window not found: "+id, Map.of("id",id)); }
    public static AppException maintenanceActivityNotFound(UUID id){ return new AppException(SupportErrorCatalog.MAINTENANCE_ACTIVITY_NOT_FOUND,"Maintenance activity not found: "+id, Map.of("id",id)); }
    public static AppException handoverPackageNotFound(UUID id){ return new AppException(SupportErrorCatalog.HANDOVER_PACKAGE_NOT_FOUND,"Handover package not found: "+id, Map.of("id",id)); }
    public static AppException handoverItemNotFound(UUID id){ return new AppException(SupportErrorCatalog.HANDOVER_ITEM_NOT_FOUND,"Handover item not found: "+id, Map.of("id",id)); }
    public static AppException workLinkNotFound(UUID id){ return new AppException(SupportErrorCatalog.WORK_LINK_NOT_FOUND,"Work link not found: "+id, Map.of("id",id)); }
    public static AppException costInputNotFound(UUID id){ return new AppException(SupportErrorCatalog.COST_INPUT_NOT_FOUND,"Cost input not found: "+id, Map.of("id",id)); }
    public static AppException escalationRuleNotFound(UUID id){ return new AppException(SupportErrorCatalog.ESCALATION_RULE_NOT_FOUND,"Escalation rule not found: "+id, Map.of("id",id)); }
    public static AppException escalationRuleCodeExists(String code){ return new AppException(SupportErrorCatalog.ESCALATION_RULE_CODE_EXISTS,"Escalation rule code exists: "+code, Map.of("code",code)); }
    public static AppException knowledgeLinkNotFound(UUID id){ return new AppException(SupportErrorCatalog.KNOWLEDGE_LINK_NOT_FOUND,"Knowledge link not found: "+id, Map.of("id",id)); }
    public static AppException requestTypeNotFound(UUID id){ return new AppException(SupportErrorCatalog.REQUEST_TYPE_NOT_FOUND,"Request type not found: "+id, Map.of("id",id)); }
    public static AppException requestTypeCodeExists(String code){ return new AppException(SupportErrorCatalog.REQUEST_TYPE_CODE_EXISTS,"Request type code exists: "+code, Map.of("code",code)); }
}

package com.company.scopery.modules.trust.retention.application.response;
import com.company.scopery.modules.trust.retention.domain.model.RetentionPolicy;
import java.util.UUID;
public record RetentionPolicyResponse(UUID id, String policyCode, String name, String objectTypeCode, int retentionPeriodDays, String retentionAction, boolean enabled) {
    public static RetentionPolicyResponse from(RetentionPolicy p){
        return new RetentionPolicyResponse(p.id(), p.policyCode(), p.name(), p.objectTypeCode(), p.retentionPeriodDays(), p.retentionAction(), p.enabled());
    }
}

package com.company.scopery.modules.profitability.shared.activity;
import com.company.scopery.common.audit.ActivityLogService; import com.company.scopery.modules.profitability.shared.constant.ProfitabilityModuleCodes;
import org.springframework.stereotype.Component; import java.util.UUID;
@Component
public class ProfitabilityActivityLogger {
    private final ActivityLogService s; public ProfitabilityActivityLogger(ActivityLogService s){this.s=s;}
    public void logSuccess(String entityType, UUID entityId, String action, String message){
        s.logSuccess(ProfitabilityModuleCodes.PROFITABILITY, entityType, entityId==null?null:entityId.toString(), action, null, null, message, null);
    }
}

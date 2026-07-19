package com.company.scopery.modules.productivity.shared.activity;
import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.productivity.shared.constant.ProductivityModuleCodes;
import org.springframework.stereotype.Component;
import java.util.UUID;
@Component
public class ProductivityActivityLogger {
    private final ActivityLogService s;
    public ProductivityActivityLogger(ActivityLogService s){this.s=s;}
    public void logSuccess(String entityType, UUID entityId, String action, String message){
        s.logSuccess(ProductivityModuleCodes.PRODUCTIVITY, entityType, entityId==null?null:entityId.toString(), action, null, null, message, null);
    }
}

package com.company.scopery.modules.configuration.shared.activity;
import com.company.scopery.common.audit.ActivityLogService; import com.company.scopery.modules.configuration.shared.constant.ConfigurationModuleCodes;
import org.springframework.stereotype.Component; import java.util.UUID;
@Component
public class ConfigurationActivityLogger {
    private final ActivityLogService s; public ConfigurationActivityLogger(ActivityLogService s){this.s=s;}
    public void logSuccess(String entityType, UUID entityId, String action, String message){
        s.logSuccess(ConfigurationModuleCodes.CONFIGURATION, entityType, entityId==null?null:entityId.toString(), action, null, null, message, null);
    }
}

package com.company.scopery.modules.raid.shared.activity;
import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.raid.shared.constant.RaidModuleCodes;
import org.springframework.stereotype.Component;
import java.util.UUID;
@Component
public class RaidActivityLogger {
    private final ActivityLogService s;
    public RaidActivityLogger(ActivityLogService s){ this.s=s; }
    public void logSuccess(String entityType, UUID entityId, String action, String message){
        s.logSuccess(RaidModuleCodes.RAID, entityType, entityId==null?null:entityId.toString(), action, null, null, message, null);
    }
}

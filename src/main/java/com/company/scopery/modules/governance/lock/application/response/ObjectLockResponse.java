package com.company.scopery.modules.governance.lock.application.response;
import com.company.scopery.modules.governance.lock.domain.model.ObjectLock;
import java.util.UUID;
public record ObjectLockResponse(UUID id, String objectTypeCode, UUID targetId, String lockType, String status) {
    public static ObjectLockResponse from(ObjectLock l) { return new ObjectLockResponse(l.id(), l.objectTypeCode(), l.targetId(), l.lockType(), l.status()); }
}

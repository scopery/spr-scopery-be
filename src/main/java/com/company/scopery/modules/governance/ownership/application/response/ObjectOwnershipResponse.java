package com.company.scopery.modules.governance.ownership.application.response;
import com.company.scopery.modules.governance.ownership.domain.model.ObjectOwnership;
import java.util.UUID;
public record ObjectOwnershipResponse(UUID id, String objectTypeCode, UUID targetId, String ownerTargetType, UUID ownerTargetId, String status) {
    public static ObjectOwnershipResponse from(ObjectOwnership o) { return new ObjectOwnershipResponse(o.id(), o.objectTypeCode(), o.targetId(), o.ownerTargetType(), o.ownerTargetId(), o.status()); }
}

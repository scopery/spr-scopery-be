package com.company.scopery.modules.governance.grant.application.response;
import com.company.scopery.modules.governance.grant.domain.model.ObjectAccessGrant;
import java.util.UUID;
public record ObjectAccessGrantResponse(UUID id, String objectTypeCode, UUID targetId, String granteeType, UUID granteeId, String grantRole, String status) {
    public static ObjectAccessGrantResponse from(ObjectAccessGrant g) {
        return new ObjectAccessGrantResponse(g.id(), g.objectTypeCode(), g.targetId(), g.granteeType(), g.granteeId(), g.grantRole(), g.status());
    }
}

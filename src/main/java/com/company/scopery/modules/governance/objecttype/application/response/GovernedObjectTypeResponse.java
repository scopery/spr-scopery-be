package com.company.scopery.modules.governance.objecttype.application.response;
import com.company.scopery.modules.governance.objecttype.domain.model.GovernedObjectType;
public record GovernedObjectTypeResponse(String objectTypeCode, boolean ownershipSupported,
        boolean versioningSupported, boolean lockingSupported, boolean restoreSupported, boolean enabled) {
    public static GovernedObjectTypeResponse from(GovernedObjectType t) {
        return new GovernedObjectTypeResponse(t.objectTypeCode(), t.ownershipSupported(),
                t.versioningSupported(), t.lockingSupported(), t.restoreSupported(), t.enabled());
    }
}

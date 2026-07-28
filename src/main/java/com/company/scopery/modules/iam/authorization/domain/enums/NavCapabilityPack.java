package com.company.scopery.modules.iam.authorization.domain.enums;

public enum NavCapabilityPack {
    NAV_ORG,
    NAV_WORKSPACE,
    NAV_PROJECT,
    NAV_SETTINGS;

    public static NavCapabilityPack fromParam(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("pack is required");
        }
        try {
            return NavCapabilityPack.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unknown capability pack: " + raw);
        }
    }
}

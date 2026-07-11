package com.company.scopery.modules.iam.shared.constant;

public record IamPermissionAction(
        String permissionCode,
        String actionCode,
        String legacyRightCode) {

    public IamPermissionAction {
        permissionCode = normalize(permissionCode, "Permission code");
        actionCode = normalize(actionCode, "Action code");
        legacyRightCode = normalize(legacyRightCode, "Legacy right code");
    }

    public static IamPermissionAction of(String permissionCode, String actionCode, String legacyRightCode) {
        return new IamPermissionAction(permissionCode, actionCode, legacyRightCode);
    }

    public String label() {
        return permissionCode + "." + actionCode;
    }

    private static String normalize(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " must not be blank");
        }
        return value.trim().toUpperCase();
    }
}

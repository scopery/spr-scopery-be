package com.company.scopery.common.audit;

// Common system-level action constants.
// Each business module defines its own module-specific action constants as String values
// and passes them to ActivityLogService.
public final class ActivityAction {

    public static final String SYSTEM_STARTUP = "SYSTEM_STARTUP";

    private ActivityAction() {}
}

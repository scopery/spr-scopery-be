package com.company.scopery.modules.integrationhub.sync.application.adapter;

public record ProviderSyncResult(
        String status,
        String message,
        boolean liveRemoteCall,
        boolean cursorAdvanceAllowed,
        String nextCursorValue,
        int itemsProcessed) {

    public static ProviderSyncResult stubNoRemote(String providerCode, String message) {
        return new ProviderSyncResult(
                "SYNC_STUB_NO_REMOTE",
                message,
                false,
                false,
                null,
                0);
    }

    public static ProviderSyncResult unsupported(String providerCode, String message) {
        return new ProviderSyncResult(
                "UNSUPPORTED_OPERATION",
                message,
                false,
                false,
                null,
                0);
    }
}

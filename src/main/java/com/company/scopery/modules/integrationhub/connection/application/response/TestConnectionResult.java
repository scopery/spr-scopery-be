package com.company.scopery.modules.integrationhub.connection.application.response;

public record TestConnectionResult(
        String status,
        String message,
        boolean configValid,
        String providerCode) {

    public static TestConnectionResult successConfigOnly(String providerCode, String message) {
        return new TestConnectionResult("SUCCESS_CONFIG_ONLY", message, true, providerCode);
    }

    public static TestConnectionResult failedConfig(String providerCode, String message) {
        return new TestConnectionResult("FAILED_CONFIG", message, false, providerCode);
    }

    public static TestConnectionResult deferred(String providerCode) {
        return new TestConnectionResult("DEFERRED", "Provider adapter not implemented", false, providerCode);
    }
}

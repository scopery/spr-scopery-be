package com.company.scopery.modules.notification.advanced.preference.http.request;
public record UpdatePreferenceRequest(String timezone, String defaultMode, Boolean digestEnabled, Boolean quietHoursEnabled, String quietHoursStart, String quietHoursEnd) {}

package com.company.scopery.modules.collaboration.minutes.http.request;
public record UpdateMinutesRequest(String summary, String decisionsSummary, String actionsSummary, String clientVisibleSummary) {}

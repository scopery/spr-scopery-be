package com.company.scopery.modules.collaboration.minutes.http.request;
public record CreateMinutesRequest(String summary, String decisionsSummary, String actionsSummary, String clientVisibleSummary) {}

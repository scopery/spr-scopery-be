package com.company.scopery.modules.airecommendation.feedback.http.request;

public record FeedbackRequest(
        Boolean helpful,
        String reasonCode,
        String comment,
        String observedOutcome
) {}

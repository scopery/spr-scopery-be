package com.company.scopery.modules.trust.accessreview.http.request;
import jakarta.validation.constraints.NotBlank;
public record CreateAccessReviewCampaignRequest(@NotBlank String name, String scopeJson) {}

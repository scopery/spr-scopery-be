package com.company.scopery.modules.clientportal.review.http.request;
import jakarta.validation.constraints.NotBlank;
public record DecideClientReviewRequest(@NotBlank String decision, String comment){}

package com.company.scopery.modules.clientportal.portalmeeting.http.request;
import jakarta.validation.constraints.NotBlank;
public record CreatePortalMeetingCommentRequest(@NotBlank String body) {}

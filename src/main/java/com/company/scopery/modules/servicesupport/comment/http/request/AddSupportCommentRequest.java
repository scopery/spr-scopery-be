package com.company.scopery.modules.servicesupport.comment.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record AddSupportCommentRequest(@NotBlank String body, String visibility, UUID authorUserId) {}

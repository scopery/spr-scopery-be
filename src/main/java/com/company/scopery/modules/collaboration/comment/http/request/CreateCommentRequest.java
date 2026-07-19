package com.company.scopery.modules.collaboration.comment.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.List; import java.util.UUID;
public record CreateCommentRequest(UUID parentCommentId, @NotBlank String body, Boolean clientVisible, List<UUID> mentionUserIds) {}

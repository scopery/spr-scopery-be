package com.company.scopery.modules.collaboration.commentthread.http.request;
import jakarta.validation.constraints.NotBlank; import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record CreateCommentThreadRequest(@NotBlank String targetType, @NotNull UUID targetId, String title, Boolean clientVisible) {}

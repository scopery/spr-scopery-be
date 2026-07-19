package com.company.scopery.modules.aiassistant.conversation.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateConversationTitleRequest(
        @NotBlank @Size(max = 200) String title
) {}

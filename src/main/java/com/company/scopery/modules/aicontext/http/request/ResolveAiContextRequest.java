package com.company.scopery.modules.aicontext.http.request;

import java.util.UUID;

public record ResolveAiContextRequest(UUID policyId, UUID projectId, UUID documentId) {}

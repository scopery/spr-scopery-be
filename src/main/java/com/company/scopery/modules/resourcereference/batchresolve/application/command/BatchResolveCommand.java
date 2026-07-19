package com.company.scopery.modules.resourcereference.batchresolve.application.command;

import java.util.List;
import java.util.UUID;

public record BatchResolveCommand(List<ResourceRef> refs) {
    public record ResourceRef(String resourceType, UUID resourceId) {}
}

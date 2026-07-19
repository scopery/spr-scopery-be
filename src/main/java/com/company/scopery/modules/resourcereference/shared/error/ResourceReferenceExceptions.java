package com.company.scopery.modules.resourcereference.shared.error;

import com.company.scopery.common.exception.AppException;
import java.util.Map;

public final class ResourceReferenceExceptions {
    private ResourceReferenceExceptions() {}

    public static AppException resourceTypeNotFound(String typeCode) {
        return new AppException(ResourceReferenceErrorCatalog.RESOURCE_TYPE_NOT_FOUND, "Resource type not found: " + typeCode, Map.of("typeCode", typeCode));
    }

    public static AppException resourceTypeAlreadyExists(String typeCode) {
        return new AppException(ResourceReferenceErrorCatalog.RESOURCE_TYPE_ALREADY_EXISTS, "Resource type already exists: " + typeCode, Map.of("typeCode", typeCode));
    }

    public static AppException resourceTypeDisabled(String typeCode) {
        return new AppException(ResourceReferenceErrorCatalog.RESOURCE_TYPE_DISABLED, "Resource type is not enabled: " + typeCode, Map.of("typeCode", typeCode));
    }

    public static AppException batchResolveLimitExceeded(int size, int max) {
        return new AppException(ResourceReferenceErrorCatalog.BATCH_RESOLVE_LIMIT_EXCEEDED, "Request has " + size + " refs, max is " + max, Map.of("size", size, "max", max));
    }
}

package com.company.scopery.modules.resourcereference.shared.constant;

import com.company.scopery.common.constant.ApiPaths;

public final class ResourceReferenceApiPaths {
    public static final String BASE = ApiPaths.BASE_PATH + "/resource-references";
    public static final String RESOURCE_TYPES = BASE + "/types";
    public static final String MENTION_SEARCH = BASE + "/mentions/search";
    public static final String BATCH_RESOLVE = BASE + "/batch-resolve";
    public static final String VALIDATE = BASE + "/validate";
    private ResourceReferenceApiPaths() {}
}

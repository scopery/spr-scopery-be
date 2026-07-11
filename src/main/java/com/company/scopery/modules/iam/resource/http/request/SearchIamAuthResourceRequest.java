package com.company.scopery.modules.iam.resource.http.request;

public record SearchIamAuthResourceRequest(
        String keyword, String resourceType, String status, int page, int size) {
    public SearchIamAuthResourceRequest {
        if (page < 0) page = 0;
        if (size <= 0) size = 20;
    }
}

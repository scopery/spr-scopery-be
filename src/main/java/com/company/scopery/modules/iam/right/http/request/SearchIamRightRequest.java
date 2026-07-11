package com.company.scopery.modules.iam.right.http.request;

public record SearchIamRightRequest(String keyword, String module, String status, int page, int size) {
    public SearchIamRightRequest {
        if (page < 0) page = 0;
        if (size <= 0) size = 50;
    }
}

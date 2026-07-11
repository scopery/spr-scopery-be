package com.company.scopery.modules.iam.user.http.request;

public record SearchIamUserRequest(String keyword, String status, int page, int size) {
    public SearchIamUserRequest {
        if (page < 0) page = 0;
        if (size <= 0) size = 20;
    }
}

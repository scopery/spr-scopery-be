package com.company.scopery.modules.iam.user.application.query;

public record SearchIamUserQuery(String keyword, String status, int page, int size) {}

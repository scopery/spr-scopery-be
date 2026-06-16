package com.company.scopery.modules.iam.right.application.query;

public record SearchIamRightQuery(String keyword, String module, String status, int page, int size) {}

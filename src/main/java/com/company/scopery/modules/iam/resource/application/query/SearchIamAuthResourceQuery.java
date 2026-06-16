package com.company.scopery.modules.iam.resource.application.query;

public record SearchIamAuthResourceQuery(String keyword, String resourceType, String status, int page, int size) {}

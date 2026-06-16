package com.company.scopery.modules.iam.resource.application.command;

public record CreateIamAuthResourceCommand(String code, String resourceType, String name, String description) {}

package com.company.scopery.modules.iam.user.application.command;

public record CreateIamUserCommand(String username, String email, String fullName, String password) {}

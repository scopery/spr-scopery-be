package com.company.scopery.modules.iam.user.application.command;

public record ChangePasswordCommand(String currentPassword, String newPassword) {
}

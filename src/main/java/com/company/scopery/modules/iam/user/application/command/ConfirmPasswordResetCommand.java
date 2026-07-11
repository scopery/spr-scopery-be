package com.company.scopery.modules.iam.user.application.command;

public record ConfirmPasswordResetCommand(String token, String newPassword) {
}

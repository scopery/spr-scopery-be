package com.company.scopery.modules.productivity.command.domain.model;
import java.time.Instant; import java.util.UUID;
public record CommandDefinition(UUID id, String code, String title, String category, String requiredPermission,
        boolean dangerous, boolean confirmationRequired, boolean enabled, int version, Instant createdAt, Instant updatedAt) {
    public static CommandDefinition create(String code, String title, String category, String requiredPermission, boolean dangerous) {
        return new CommandDefinition(UUID.randomUUID(), code, title, category, requiredPermission, dangerous, dangerous, true, 0, null, null);
    }
}

package com.company.scopery.modules.productivity.command.application.response;
import com.company.scopery.modules.productivity.command.domain.model.CommandDefinition;
import java.util.UUID;
public record CommandDefinitionResponse(UUID id, String code, String title, String category, boolean dangerous, boolean confirmationRequired) {
    public static CommandDefinitionResponse from(CommandDefinition c) {
        return new CommandDefinitionResponse(c.id(), c.code(), c.title(), c.category(), c.dangerous(), c.confirmationRequired());
    }
}

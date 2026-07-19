package com.company.scopery.modules.productivity.command.domain.model;
import java.util.List;
public interface CommandDefinitionRepository {
    CommandDefinition save(CommandDefinition c);
    List<CommandDefinition> findEnabled();
}

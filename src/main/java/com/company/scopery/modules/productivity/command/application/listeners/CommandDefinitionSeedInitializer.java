package com.company.scopery.modules.productivity.command.application.listeners;
import com.company.scopery.modules.productivity.command.domain.model.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Component @Order(42)
public class CommandDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final CommandDefinitionRepository commands;
    public CommandDefinitionSeedInitializer(CommandDefinitionRepository commands) { this.commands = commands; }
    @Override @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (!commands.findEnabled().isEmpty()) return;
        for (var s : List.of(
                CommandDefinition.create("OPEN_SEARCH", "Open search", "NAVIGATION", "GLOBAL_SEARCH_USE", false),
                CommandDefinition.create("OPEN_INBOX", "Open work inbox", "PRODUCTIVITY", "WORK_INBOX_VIEW", false),
                CommandDefinition.create("CREATE_TASK", "Create task", "PROJECT", "PROJECT_TASK_CREATE", false)
        )) {
            commands.save(s);
        }
    }
}

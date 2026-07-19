package com.company.scopery.modules.productivity.command.application.service;
import com.company.scopery.modules.productivity.command.application.response.CommandDefinitionResponse;
import com.company.scopery.modules.productivity.command.domain.model.CommandDefinitionRepository;
import com.company.scopery.modules.productivity.shared.authorization.ProductivityAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.Locale; import java.util.UUID;
@Service
public class CommandQueryService {
    private final CommandDefinitionRepository commands; private final ProductivityAuthorizationService authorization;
    public CommandQueryService(CommandDefinitionRepository commands, ProductivityAuthorizationService authorization) {
        this.commands=commands; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<CommandDefinitionResponse> list(UUID workspaceId) {
        authorization.requireNavView(workspaceId);
        return commands.findEnabled().stream().map(CommandDefinitionResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public List<CommandDefinitionResponse> suggestions(UUID workspaceId, String q) {
        authorization.requireNavView(workspaceId);
        String query = q == null ? "" : q.toLowerCase(Locale.ROOT);
        return commands.findEnabled().stream()
                .filter(c -> query.isBlank() || c.title().toLowerCase(Locale.ROOT).contains(query) || c.code().toLowerCase(Locale.ROOT).contains(query))
                .map(CommandDefinitionResponse::from).toList();
    }
}

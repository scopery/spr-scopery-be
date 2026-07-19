package com.company.scopery.modules.productivity.command.infrastructure.mapper;
import com.company.scopery.modules.productivity.command.domain.model.CommandDefinition;
import com.company.scopery.modules.productivity.command.infrastructure.persistence.CommandDefinitionJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class CommandDefinitionPersistenceMapper {
    public CommandDefinition toDomain(CommandDefinitionJpaEntity e) {
        return new CommandDefinition(e.getId(), e.getCode(), e.getTitle(), e.getCategory(), e.getRequiredPermission(),
                e.isDangerous(), e.isConfirmationRequired(), e.isEnabled(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public CommandDefinitionJpaEntity toJpa(CommandDefinition d) {
        CommandDefinitionJpaEntity e = new CommandDefinitionJpaEntity();
        e.setId(d.id()); e.setCode(d.code()); e.setTitle(d.title()); e.setCategory(d.category()); e.setRequiredPermission(d.requiredPermission());
        e.setDangerous(d.dangerous()); e.setConfirmationRequired(d.confirmationRequired()); e.setEnabled(d.enabled());
        if (d.createdAt()!=null) { e.setCreatedAt(d.createdAt()); e.setVersion(d.version()); }
        return e;
    }
}

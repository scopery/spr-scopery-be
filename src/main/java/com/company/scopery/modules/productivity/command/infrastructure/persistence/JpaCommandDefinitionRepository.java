package com.company.scopery.modules.productivity.command.infrastructure.persistence;
import com.company.scopery.modules.productivity.command.domain.model.*;
import com.company.scopery.modules.productivity.command.infrastructure.mapper.CommandDefinitionPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaCommandDefinitionRepository implements CommandDefinitionRepository {
    private final SpringDataCommandDefinitionJpaRepository springData; private final CommandDefinitionPersistenceMapper mapper;
    public JpaCommandDefinitionRepository(SpringDataCommandDefinitionJpaRepository springData, CommandDefinitionPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public CommandDefinition save(CommandDefinition c) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(c))); }
    @Override public List<CommandDefinition> findEnabled() { return springData.findByEnabledTrueOrderByTitleAsc().stream().map(mapper::toDomain).toList(); }
}

package com.company.scopery.modules.servicesupport.escalation.infrastructure.persistence;
import com.company.scopery.modules.servicesupport.escalation.domain.model.SupportEscalationRule;
import com.company.scopery.modules.servicesupport.escalation.domain.model.SupportEscalationRuleRepository;
import com.company.scopery.modules.servicesupport.escalation.infrastructure.mapper.SupportEscalationRulePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaSupportEscalationRuleRepository implements SupportEscalationRuleRepository {
    private final SpringDataSupportEscalationRuleJpaRepository spring;
    private final SupportEscalationRulePersistenceMapper mapper;
    public JpaSupportEscalationRuleRepository(SpringDataSupportEscalationRuleJpaRepository spring, SupportEscalationRulePersistenceMapper mapper){
        this.spring=spring; this.mapper=mapper;
    }
    @Override public SupportEscalationRule save(SupportEscalationRule d){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(d))); }
    @Override public Optional<SupportEscalationRule> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public List<SupportEscalationRule> findByWorkspaceId(UUID w){ return spring.findByWorkspaceId(w).stream().map(mapper::toDomain).toList(); }
    @Override public boolean existsByWorkspaceIdAndRuleCode(UUID w, String c){ return spring.existsByWorkspaceIdAndRuleCode(w, c); }
}

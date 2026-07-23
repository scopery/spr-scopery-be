package com.company.scopery.modules.traceability.businessrule.infrastructure.persistence;

import com.company.scopery.modules.traceability.businessrule.domain.model.BusinessRule;
import com.company.scopery.modules.traceability.businessrule.domain.model.BusinessRuleRepository;
import com.company.scopery.modules.traceability.businessrule.infrastructure.mapper.BusinessRulePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaBusinessRuleRepository implements BusinessRuleRepository {

    private final SpringDataBusinessRuleJpaRepository springData;
    private final BusinessRulePersistenceMapper mapper;

    public JpaBusinessRuleRepository(
            SpringDataBusinessRuleJpaRepository springData,
            BusinessRulePersistenceMapper mapper
    ) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public BusinessRule save(BusinessRule rule) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(rule)));
    }

    @Override
    public Optional<BusinessRule> findByIdAndFunctionalItemId(UUID id, UUID functionalItemId) {
        return springData.findByIdAndFunctionalItemId(id, functionalItemId).map(mapper::toDomain);
    }

    @Override
    public List<BusinessRule> findByFunctionalItemId(UUID functionalItemId) {
        return springData.findByFunctionalItemIdOrderByCreatedAtDesc(functionalItemId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByFunctionalItemIdAndCode(UUID functionalItemId, String code) {
        return springData.existsByFunctionalItemIdAndCode(functionalItemId, code);
    }

    @Override
    public void delete(UUID id, UUID functionalItemId) {
        springData.deleteByIdAndFunctionalItemId(id, functionalItemId);
    }
}

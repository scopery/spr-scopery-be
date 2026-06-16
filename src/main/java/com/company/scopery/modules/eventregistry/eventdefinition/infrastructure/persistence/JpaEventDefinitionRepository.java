package com.company.scopery.modules.eventregistry.eventdefinition.infrastructure.persistence;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.*;
import com.company.scopery.modules.eventregistry.eventdefinition.infrastructure.mapper.EventDefinitionPersistenceMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaEventDefinitionRepository implements EventDefinitionRepository {

    private final SpringDataEventDefinitionJpaRepository springDataRepository;
    private final SpringDataEventVariableJpaRepository variableRepository;
    private final EventDefinitionPersistenceMapper mapper;

    public JpaEventDefinitionRepository(SpringDataEventDefinitionJpaRepository springDataRepository,
                                         SpringDataEventVariableJpaRepository variableRepository,
                                         EventDefinitionPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.variableRepository = variableRepository;
        this.mapper = mapper;
    }

    @Override
    public EventDefinition save(EventDefinition eventDefinition) {
        EventDefinitionJpaEntity entity = mapper.toJpaEntity(eventDefinition);
        EventDefinitionJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<EventDefinition> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<EventDefinition> findByCode(EventDefinitionCode code) {
        return springDataRepository.findByCode(code.value()).map(mapper::toDomain);
    }

    @Override
    public boolean existsByCode(EventDefinitionCode code) {
        return springDataRepository.existsByCode(code.value());
    }

    @Override
    public boolean existsBySourceSystemAndEventKey(SourceSystemCode sourceSystem, EventKey eventKey) {
        return springDataRepository.existsBySourceSystemAndEventKey(sourceSystem.value(), eventKey.value());
    }

    @Override
    public Optional<EventDefinition> findBySourceSystemAndEventKey(SourceSystemCode sourceSystem, EventKey eventKey) {
        return springDataRepository.findBySourceSystemAndEventKey(sourceSystem.value(), eventKey.value())
                .map(mapper::toDomain);
    }

    @Override
    public Page<EventDefinition> findAll(String keyword, String sourceSystem, String eventKey,
                                          EventDefinitionStatus status, Pageable pageable) {
        Specification<EventDefinitionJpaEntity> spec = buildSearchSpec(keyword, sourceSystem, eventKey, status);
        return springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
    }

    @Override
    public List<EventVariable> findVariablesByEventDefinitionId(UUID eventDefinitionId) {
        return variableRepository.findByEventDefinitionId(eventDefinitionId)
                .stream().map(mapper::toVariableDomain).toList();
    }

    @Override
    public EventVariable saveVariable(EventVariable variable) {
        EventVariableJpaEntity entity = mapper.toVariableJpaEntity(variable);
        EventVariableJpaEntity saved = variableRepository.saveAndFlush(entity);
        return mapper.toVariableDomain(saved);
    }

    @Override
    @Transactional
    public void deleteVariablesByEventDefinitionId(UUID eventDefinitionId) {
        variableRepository.deleteByEventDefinitionId(eventDefinitionId);
    }

    private Specification<EventDefinitionJpaEntity> buildSearchSpec(String keyword, String sourceSystem,
                                                                     String eventKey,
                                                                     EventDefinitionStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.trim().toUpperCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.upper(root.get("name")), pattern),
                        cb.like(root.get("code"), pattern)
                ));
            }
            if (sourceSystem != null) {
                predicates.add(cb.equal(root.get("sourceSystem"), sourceSystem));
            }
            if (eventKey != null) {
                predicates.add(cb.equal(root.get("eventKey"), eventKey));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

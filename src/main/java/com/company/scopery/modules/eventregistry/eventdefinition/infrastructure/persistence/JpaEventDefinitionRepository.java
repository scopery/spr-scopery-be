package com.company.scopery.modules.eventregistry.eventdefinition.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDefinitionStatus;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventVariable;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventDefinitionCode;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventKey;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.SourceSystemCode;
import com.company.scopery.modules.eventregistry.eventdefinition.infrastructure.mapper.EventDefinitionPersistenceMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public PageResult<EventDefinition> findAll(String keyword, String sourceSystem, String eventKey,
                                                EventDefinitionStatus status, PageQuery pageQuery) {
        Specification<EventDefinitionJpaEntity> spec = buildSearchSpec(keyword, sourceSystem, eventKey, status);
        Pageable pageable = toPageable(pageQuery);
        Page<EventDefinition> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
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
        variableRepository.deleteByEventDefinitionIdBulk(eventDefinitionId);
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

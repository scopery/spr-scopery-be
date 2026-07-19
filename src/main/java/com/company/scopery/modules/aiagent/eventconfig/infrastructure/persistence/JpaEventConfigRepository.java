package com.company.scopery.modules.aiagent.eventconfig.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.eventconfig.domain.enums.EventConfigEnvironment;
import com.company.scopery.modules.aiagent.eventconfig.domain.enums.EventConfigStatus;
import com.company.scopery.modules.aiagent.eventconfig.domain.enums.EventTriggerType;
import com.company.scopery.modules.aiagent.eventconfig.domain.model.EventConfig;
import com.company.scopery.modules.aiagent.eventconfig.domain.model.EventConfigRepository;
import com.company.scopery.modules.aiagent.eventconfig.domain.valueobject.EventConfigCode;
import com.company.scopery.modules.aiagent.eventconfig.infrastructure.persistence.entity.EventConfigJpaEntity;
import com.company.scopery.modules.aiagent.eventconfig.infrastructure.mapper.EventConfigPersistenceMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaEventConfigRepository implements EventConfigRepository {

    private final SpringDataEventConfigJpaRepository springDataRepository;
    private final EventConfigPersistenceMapper mapper;

    public JpaEventConfigRepository(SpringDataEventConfigJpaRepository springDataRepository,
                                     EventConfigPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public EventConfig save(EventConfig eventConfig) {
        EventConfigJpaEntity entity = mapper.toJpaEntity(eventConfig);
        EventConfigJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<EventConfig> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<EventConfig> findAllByStatus(EventConfigStatus status) {
        return springDataRepository.findByStatus(status.name()).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByCode(EventConfigCode code) {
        return springDataRepository.existsByCode(code.value());
    }

    @Override
    public boolean existsActiveByEventDefinitionIdAndEnvironment(UUID eventDefinitionId,
                                                                  EventConfigEnvironment environment,
                                                                  UUID excludeId) {
        Specification<EventConfigJpaEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("eventDefinitionId"), eventDefinitionId));
            predicates.add(cb.equal(root.get("environment"), environment.name()));
            predicates.add(cb.equal(root.get("status"), EventConfigStatus.ACTIVE.name()));
            if (excludeId != null) {
                predicates.add(cb.notEqual(root.get("id"), excludeId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return springDataRepository.count(spec) > 0;
    }

    @Override
    public Optional<EventConfig> findActiveByEventDefinitionIdAndEnvironment(UUID eventDefinitionId,
                                                                              EventConfigEnvironment environment) {
        Specification<EventConfigJpaEntity> spec = (root, query, cb) -> cb.and(
                cb.equal(root.get("eventDefinitionId"), eventDefinitionId),
                cb.equal(root.get("environment"), environment.name()),
                cb.equal(root.get("status"), EventConfigStatus.ACTIVE.name())
        );
        return springDataRepository.findOne(spec).map(mapper::toDomain);
    }

    @Override
    public boolean existsActiveByEventDefinitionId(UUID eventDefinitionId) {
        Specification<EventConfigJpaEntity> spec = (root, query, cb) -> cb.and(
                cb.equal(root.get("eventDefinitionId"), eventDefinitionId),
                cb.equal(root.get("status"), EventConfigStatus.ACTIVE.name())
        );
        return springDataRepository.count(spec) > 0;
    }

    @Override
    public PageResult<EventConfig> findAll(String keyword, UUID eventDefinitionId,
                                      EventConfigEnvironment environment, EventTriggerType triggerType,
                                      EventConfigStatus status, UUID agentId, PageQuery pageQuery) {
        Specification<EventConfigJpaEntity> spec =
                buildSearchSpec(keyword, eventDefinitionId, environment, triggerType, status, agentId);
        Pageable pageable = toPageable(pageQuery);
        Page<EventConfig> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }

    private Specification<EventConfigJpaEntity> buildSearchSpec(String keyword, UUID eventDefinitionId,
                                                                  EventConfigEnvironment environment,
                                                                  EventTriggerType triggerType,
                                                                  EventConfigStatus status, UUID agentId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.trim().toUpperCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.upper(root.get("name")), pattern),
                        cb.like(root.get("code"), pattern)
                ));
            }
            if (eventDefinitionId != null) {
                predicates.add(cb.equal(root.get("eventDefinitionId"), eventDefinitionId));
            }
            if (environment != null) {
                predicates.add(cb.equal(root.get("environment"), environment.name()));
            }
            if (triggerType != null) {
                predicates.add(cb.equal(root.get("triggerType"), triggerType.name()));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            if (agentId != null) {
                predicates.add(cb.equal(root.get("agentId"), agentId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

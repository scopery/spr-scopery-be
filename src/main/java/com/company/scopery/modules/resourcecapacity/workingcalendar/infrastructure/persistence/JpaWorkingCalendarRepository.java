package com.company.scopery.modules.resourcecapacity.workingcalendar.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.enums.WorkingCalendarStatus;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendar;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendarRepository;
import com.company.scopery.modules.resourcecapacity.workingcalendar.infrastructure.mapper.WorkingCalendarPersistenceMapper;
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
public class JpaWorkingCalendarRepository implements WorkingCalendarRepository {

    private final SpringDataWorkingCalendarJpaRepository springDataRepository;
    private final WorkingCalendarPersistenceMapper mapper;

    public JpaWorkingCalendarRepository(SpringDataWorkingCalendarJpaRepository springDataRepository,
                                        WorkingCalendarPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public WorkingCalendar save(WorkingCalendar calendar) {
        WorkingCalendarJpaEntity entity = mapper.toJpaEntity(calendar);
        WorkingCalendarJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<WorkingCalendar> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<WorkingCalendar> findByWorkspaceIdAndCode(UUID workspaceId, String code) {
        return springDataRepository.findByWorkspaceIdAndCode(workspaceId, code).map(mapper::toDomain);
    }

    @Override
    public boolean existsByWorkspaceIdAndCode(UUID workspaceId, String code) {
        return springDataRepository.existsByWorkspaceIdAndCode(workspaceId, code);
    }

    @Override
    public Optional<WorkingCalendar> findDefaultActiveByWorkspaceId(UUID workspaceId) {
        return springDataRepository
                .findByWorkspaceIdAndIsDefaultTrueAndStatus(workspaceId, WorkingCalendarStatus.ACTIVE.name())
                .map(mapper::toDomain);
    }

    @Override
    public List<WorkingCalendar> findAllActiveDefaultsByWorkspaceId(UUID workspaceId) {
        return springDataRepository
                .findAllByWorkspaceIdAndIsDefaultTrueAndStatus(workspaceId, WorkingCalendarStatus.ACTIVE.name())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean isReferencedByCapacityProfiles(UUID calendarId) {
        return springDataRepository.existsProfileByCalendarId(calendarId);
    }

    @Override
    public PageResult<WorkingCalendar> search(UUID workspaceId, WorkingCalendarStatus status, Boolean isDefault,
                                              String code, PageQuery pageQuery) {
        Specification<WorkingCalendarJpaEntity> spec = buildSearchSpec(workspaceId, status, isDefault, code);
        Pageable pageable = toPageable(pageQuery);
        Page<WorkingCalendar> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }

    private Specification<WorkingCalendarJpaEntity> buildSearchSpec(UUID workspaceId, WorkingCalendarStatus status,
                                                                     Boolean isDefault, String code) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("workspaceId"), workspaceId));
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            if (isDefault != null) {
                predicates.add(cb.equal(root.get("isDefault"), isDefault));
            }
            if (code != null && !code.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("code")), "%" + code.toLowerCase() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

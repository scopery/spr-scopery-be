package com.company.scopery.modules.resourcecapacity.calendarexception.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.resourcecapacity.calendarexception.domain.model.CalendarException;
import com.company.scopery.modules.resourcecapacity.calendarexception.domain.model.CalendarExceptionRepository;
import com.company.scopery.modules.resourcecapacity.calendarexception.infrastructure.mapper.CalendarExceptionPersistenceMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaCalendarExceptionRepository implements CalendarExceptionRepository {

    private final SpringDataCalendarExceptionJpaRepository springDataRepository;
    private final CalendarExceptionPersistenceMapper mapper;

    public JpaCalendarExceptionRepository(SpringDataCalendarExceptionJpaRepository springDataRepository,
                                          CalendarExceptionPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public CalendarException save(CalendarException exception) {
        CalendarExceptionJpaEntity entity = mapper.toJpaEntity(exception);
        CalendarExceptionJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<CalendarException> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByWorkingCalendarIdAndExceptionDate(UUID workingCalendarId, LocalDate exceptionDate) {
        return springDataRepository.existsByWorkingCalendarIdAndExceptionDate(workingCalendarId, exceptionDate);
    }

    @Override
    public Optional<CalendarException> findByWorkingCalendarIdAndExceptionDate(UUID workingCalendarId, LocalDate exceptionDate) {
        return springDataRepository.findByWorkingCalendarIdAndExceptionDate(workingCalendarId, exceptionDate)
                .map(mapper::toDomain);
    }

    @Override
    public List<CalendarException> findByWorkingCalendarIdAndDateRange(UUID workingCalendarId, LocalDate from, LocalDate to) {
        return springDataRepository.findByWorkingCalendarIdAndExceptionDateBetween(workingCalendarId, from, to)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public PageResult<CalendarException> search(UUID workingCalendarId, LocalDate from, LocalDate to, PageQuery pageQuery) {
        Specification<CalendarExceptionJpaEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("workingCalendarId"), workingCalendarId));
            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("exceptionDate"), from));
            }
            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("exceptionDate"), to));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.by(Sort.Direction.ASC, "exceptionDate");
        Pageable pageable = PageRequest.of(pageQuery.page(), pageQuery.size(), sort);

        Page<CalendarException> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    @Override
    public void deleteById(UUID id) {
        springDataRepository.deleteById(id);
    }
}

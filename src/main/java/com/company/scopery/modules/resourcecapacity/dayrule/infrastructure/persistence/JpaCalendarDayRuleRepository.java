package com.company.scopery.modules.resourcecapacity.dayrule.infrastructure.persistence;

import com.company.scopery.modules.resourcecapacity.dayrule.domain.model.CalendarDayRule;
import com.company.scopery.modules.resourcecapacity.dayrule.domain.model.CalendarDayRuleRepository;
import com.company.scopery.modules.resourcecapacity.dayrule.infrastructure.mapper.CalendarDayRulePersistenceMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaCalendarDayRuleRepository implements CalendarDayRuleRepository {

    private final SpringDataCalendarDayRuleJpaRepository springDataRepository;
    private final CalendarDayRulePersistenceMapper mapper;

    public JpaCalendarDayRuleRepository(SpringDataCalendarDayRuleJpaRepository springDataRepository,
                                        CalendarDayRulePersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public List<CalendarDayRule> findByWorkingCalendarId(UUID workingCalendarId) {
        return springDataRepository.findByWorkingCalendarId(workingCalendarId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public List<CalendarDayRule> replaceAll(UUID workingCalendarId, List<CalendarDayRule> rules) {
        springDataRepository.deleteByWorkingCalendarId(workingCalendarId);
        List<CalendarDayRuleJpaEntity> entities = rules.stream().map(mapper::toJpaEntity).toList();
        List<CalendarDayRuleJpaEntity> saved = springDataRepository.saveAllAndFlush(entities);
        return saved.stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public void deleteByWorkingCalendarId(UUID workingCalendarId) {
        springDataRepository.deleteByWorkingCalendarId(workingCalendarId);
    }
}

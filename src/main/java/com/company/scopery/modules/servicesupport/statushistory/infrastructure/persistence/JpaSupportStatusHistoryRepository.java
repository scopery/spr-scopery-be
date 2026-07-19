package com.company.scopery.modules.servicesupport.statushistory.infrastructure.persistence;
import com.company.scopery.modules.servicesupport.statushistory.domain.model.SupportStatusHistory;
import com.company.scopery.modules.servicesupport.statushistory.domain.model.SupportStatusHistoryRepository;
import com.company.scopery.modules.servicesupport.statushistory.infrastructure.mapper.SupportStatusHistoryPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.UUID;
@Repository
public class JpaSupportStatusHistoryRepository implements SupportStatusHistoryRepository {
    private final SpringDataSupportStatusHistoryJpaRepository spring;
    private final SupportStatusHistoryPersistenceMapper mapper;
    public JpaSupportStatusHistoryRepository(SpringDataSupportStatusHistoryJpaRepository spring, SupportStatusHistoryPersistenceMapper mapper){
        this.spring=spring; this.mapper=mapper;
    }
    @Override public SupportStatusHistory save(SupportStatusHistory d){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(d))); }
    @Override public List<SupportStatusHistory> findBySupportCaseId(UUID caseId){ return spring.findBySupportCaseId(caseId).stream().map(mapper::toDomain).toList(); }
}

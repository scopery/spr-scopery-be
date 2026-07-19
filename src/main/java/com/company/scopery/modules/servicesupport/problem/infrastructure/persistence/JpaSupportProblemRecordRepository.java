package com.company.scopery.modules.servicesupport.problem.infrastructure.persistence;
import com.company.scopery.modules.servicesupport.problem.domain.model.SupportProblemRecord;
import com.company.scopery.modules.servicesupport.problem.domain.model.SupportProblemRecordRepository;
import com.company.scopery.modules.servicesupport.problem.infrastructure.mapper.SupportProblemRecordPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaSupportProblemRecordRepository implements SupportProblemRecordRepository {
    private final SpringDataSupportProblemRecordJpaRepository spring;
    private final SupportProblemRecordPersistenceMapper mapper;
    public JpaSupportProblemRecordRepository(SpringDataSupportProblemRecordJpaRepository spring, SupportProblemRecordPersistenceMapper mapper){
        this.spring=spring; this.mapper=mapper;
    }
    @Override public SupportProblemRecord save(SupportProblemRecord d){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(d))); }
    @Override public Optional<SupportProblemRecord> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public List<SupportProblemRecord> findByWorkspaceId(UUID w){ return spring.findByWorkspaceId(w).stream().map(mapper::toDomain).toList(); }
}

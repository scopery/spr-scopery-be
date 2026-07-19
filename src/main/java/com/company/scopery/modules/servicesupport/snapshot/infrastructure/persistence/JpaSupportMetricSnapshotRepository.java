package com.company.scopery.modules.servicesupport.snapshot.infrastructure.persistence;
import com.company.scopery.modules.servicesupport.snapshot.domain.model.SupportMetricSnapshot;
import com.company.scopery.modules.servicesupport.snapshot.domain.model.SupportMetricSnapshotRepository;
import com.company.scopery.modules.servicesupport.snapshot.infrastructure.mapper.SupportMetricSnapshotPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaSupportMetricSnapshotRepository implements SupportMetricSnapshotRepository {
    private final SpringDataSupportMetricSnapshotJpaRepository spring;
    private final SupportMetricSnapshotPersistenceMapper mapper;
    public JpaSupportMetricSnapshotRepository(SpringDataSupportMetricSnapshotJpaRepository spring, SupportMetricSnapshotPersistenceMapper mapper){
        this.spring=spring; this.mapper=mapper;
    }
    @Override public SupportMetricSnapshot save(SupportMetricSnapshot d){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(d))); }
    @Override public Optional<SupportMetricSnapshot> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public List<SupportMetricSnapshot> findByWorkspaceId(UUID w){ return spring.findByWorkspaceId(w).stream().map(mapper::toDomain).toList(); }
}

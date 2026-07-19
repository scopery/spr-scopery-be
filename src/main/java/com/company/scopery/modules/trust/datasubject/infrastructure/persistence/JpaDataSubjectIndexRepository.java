package com.company.scopery.modules.trust.datasubject.infrastructure.persistence;
import com.company.scopery.modules.trust.datasubject.domain.model.DataSubjectIndex;
import com.company.scopery.modules.trust.datasubject.domain.model.DataSubjectIndexRepository;
import com.company.scopery.modules.trust.datasubject.infrastructure.mapper.DataSubjectIndexPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaDataSubjectIndexRepository implements DataSubjectIndexRepository {
    private final SpringDataDataSubjectIndexJpaRepository spring;
    private final DataSubjectIndexPersistenceMapper mapper;
    public JpaDataSubjectIndexRepository(SpringDataDataSubjectIndexJpaRepository spring, DataSubjectIndexPersistenceMapper mapper) {
        this.spring = spring; this.mapper = mapper;
    }
    @Override public DataSubjectIndex save(DataSubjectIndex d) {
        return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(d)));
    }
    @Override public Optional<DataSubjectIndex> findById(UUID id) {
        return spring.findById(id).map(mapper::toDomain);
    }
    @Override public List<DataSubjectIndex> findByWorkspaceId(UUID workspaceId) {
        return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList();
    }
}

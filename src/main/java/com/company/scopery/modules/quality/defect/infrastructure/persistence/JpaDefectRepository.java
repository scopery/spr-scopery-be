package com.company.scopery.modules.quality.defect.infrastructure.persistence;
import com.company.scopery.modules.quality.defect.domain.model.Defect;
import com.company.scopery.modules.quality.defect.domain.model.DefectRepository;
import com.company.scopery.modules.quality.defect.infrastructure.mapper.DefectPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaDefectRepository implements DefectRepository {
    private final SpringDataDefectJpaRepository springData;
    private final DefectPersistenceMapper mapper;
    public JpaDefectRepository(SpringDataDefectJpaRepository springData, DefectPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public Defect save(Defect e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<Defect> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
    @Override public List<Defect> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }
    @Override public List<Defect> findOpenBlockers(UUID projectId) {
        return springData.findOpenBlockers(projectId).stream().map(mapper::toDomain).toList();
    }
}

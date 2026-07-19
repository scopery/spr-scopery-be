package com.company.scopery.modules.quality.defectlink.infrastructure.persistence;
import com.company.scopery.modules.quality.defectlink.domain.model.*;
import com.company.scopery.modules.quality.defectlink.infrastructure.mapper.DefectLinkPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaDefectLinkRepository implements DefectLinkRepository {
    private final SpringDataDefectLinkJpaRepository springData;
    private final DefectLinkPersistenceMapper mapper;
    public JpaDefectLinkRepository(SpringDataDefectLinkJpaRepository springData, DefectLinkPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public DefectLink save(DefectLink e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<DefectLink> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
    @Override public List<DefectLink> findByProjectIdAndDefectId(UUID projectId, UUID defectId) {
        return springData.findByProjectIdAndDefectIdOrderByCreatedAtDesc(projectId, defectId).stream().map(mapper::toDomain).toList();
    }
}

package com.company.scopery.modules.quality.nfrspecification.infrastructure.persistence;
import com.company.scopery.modules.quality.nfrspecification.domain.model.*;
import com.company.scopery.modules.quality.nfrspecification.infrastructure.mapper.NfrTargetPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.UUID;
@Repository
public class JpaNfrTargetRepository implements NfrTargetRepository {
    private final SpringDataNfrTargetJpaRepository springData;
    private final NfrTargetPersistenceMapper mapper;
    public JpaNfrTargetRepository(SpringDataNfrTargetJpaRepository springData, NfrTargetPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public List<NfrTarget> findByRequirementId(UUID requirementId) {
        return springData.findByRequirementIdOrderByDisplayOrderAsc(requirementId).stream().map(mapper::toDomain).toList();
    }
    @Override public List<NfrTarget> saveAll(List<NfrTarget> targets) {
        return springData.saveAllAndFlush(targets.stream().map(mapper::toJpaEntity).toList())
                .stream().map(mapper::toDomain).toList();
    }
    @Override public void deleteByRequirementId(UUID requirementId) {
        springData.deleteByRequirementId(requirementId);
    }
}

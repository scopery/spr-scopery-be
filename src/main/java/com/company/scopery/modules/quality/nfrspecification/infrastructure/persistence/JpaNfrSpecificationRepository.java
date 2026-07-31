package com.company.scopery.modules.quality.nfrspecification.infrastructure.persistence;
import com.company.scopery.modules.quality.nfrspecification.domain.model.*;
import com.company.scopery.modules.quality.nfrspecification.infrastructure.mapper.NfrSpecificationPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.Optional; import java.util.UUID;
@Repository
public class JpaNfrSpecificationRepository implements NfrSpecificationRepository {
    private final SpringDataNfrSpecificationJpaRepository springData;
    private final NfrSpecificationPersistenceMapper mapper;
    public JpaNfrSpecificationRepository(SpringDataNfrSpecificationJpaRepository springData,
            NfrSpecificationPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public Optional<NfrSpecification> findByRequirementId(UUID requirementId) {
        return springData.findById(requirementId).map(mapper::toDomain);
    }
    @Override public NfrSpecification save(NfrSpecification e) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e)));
    }
}

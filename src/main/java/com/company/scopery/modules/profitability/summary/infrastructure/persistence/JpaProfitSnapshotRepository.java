package com.company.scopery.modules.profitability.summary.infrastructure.persistence;
import com.company.scopery.modules.profitability.summary.domain.model.*;
import com.company.scopery.modules.profitability.summary.infrastructure.mapper.ProfitSnapshotPersistenceMapper;
import org.springframework.stereotype.Repository;
@Repository
public class JpaProfitSnapshotRepository implements ProfitSnapshotRepository {
    private final SpringDataProfitSnapshotJpaRepository springData;
    private final ProfitSnapshotPersistenceMapper mapper;
    public JpaProfitSnapshotRepository(SpringDataProfitSnapshotJpaRepository springData, ProfitSnapshotPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public ProfitSnapshot save(ProfitSnapshot s) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(s)));
    }
}

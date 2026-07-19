package com.company.scopery.modules.profitability.ratecard.infrastructure.persistence;

import com.company.scopery.modules.profitability.ratecard.domain.model.ProfitRateCard;
import com.company.scopery.modules.profitability.ratecard.domain.model.ProfitRateCardRepository;
import com.company.scopery.modules.profitability.ratecard.infrastructure.mapper.ProfitRateCardPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProfitRateCardRepository implements ProfitRateCardRepository {
    private final SpringDataProfitRateCardJpaRepository springData;
    private final ProfitRateCardPersistenceMapper mapper;

    public JpaProfitRateCardRepository(SpringDataProfitRateCardJpaRepository springData,
                                       ProfitRateCardPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public ProfitRateCard save(ProfitRateCard rateCard) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(rateCard)));
    }

    @Override
    public Optional<ProfitRateCard> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ProfitRateCard> findByWorkspaceId(UUID workspaceId) {
        return springData.findByWorkspaceIdOrderByCreatedAtDesc(workspaceId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ProfitRateCard> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByWorkspaceIdAndProjectIdAndRateCode(UUID workspaceId, UUID projectId, String rateCode) {
        return springData.existsByWorkspaceIdAndProjectIdAndRateCode(workspaceId, projectId, rateCode);
    }
}

package com.company.scopery.modules.traceability.aimapping.summary.infrastructure.persistence;

import com.company.scopery.modules.traceability.aimapping.shared.constant.AiMappingTableNames;
import com.company.scopery.modules.traceability.aimapping.summary.domain.enums.SummaryEntityType;
import com.company.scopery.modules.traceability.aimapping.summary.domain.model.MappingSummary;
import com.company.scopery.modules.traceability.aimapping.summary.domain.model.MappingSummaryRepository;
import com.company.scopery.modules.traceability.aimapping.summary.infrastructure.mapper.MappingSummaryPersistenceMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Types;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaMappingSummaryRepository implements MappingSummaryRepository {

    private final SpringDataMappingSummaryJpaRepository springDataRepository;
    private final MappingSummaryPersistenceMapper mapper;
    private final JdbcTemplate jdbc;

    public JpaMappingSummaryRepository(SpringDataMappingSummaryJpaRepository springDataRepository,
                                       MappingSummaryPersistenceMapper mapper,
                                       JdbcTemplate jdbc) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
        this.jdbc = jdbc;
    }

    @Override
    public MappingSummary save(MappingSummary summary) {
        MappingSummaryJpaEntity entity = mapper.toJpaEntity(summary);
        MappingSummaryJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<MappingSummary> findByEntityTypeAndEntityId(SummaryEntityType entityType, UUID entityId) {
        return springDataRepository
                .findByEntityTypeAndEntityId(entityType.name(), entityId)
                .map(mapper::toDomain);
    }

    @Override
    public List<MappingSummary> findByEntityTypeAndEntityIdIn(SummaryEntityType entityType, List<UUID> entityIds) {
        return springDataRepository
                .findByEntityTypeAndEntityIdIn(entityType.name(), entityIds)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void updateEmbedding(UUID id, float[] embedding) {
        String vectorLiteral = embeddingToString(embedding);
        if (vectorLiteral == null) {
            jdbc.update("UPDATE " + AiMappingTableNames.MAPPING_SUMMARY + " SET embedding = NULL WHERE id = CAST(? AS uuid)",
                    id.toString());
        } else {
            jdbc.update("UPDATE " + AiMappingTableNames.MAPPING_SUMMARY + " SET embedding = CAST(? AS vector) WHERE id = CAST(? AS uuid)",
                    new Object[]{vectorLiteral, id.toString()},
                    new int[]{Types.VARCHAR, Types.VARCHAR});
        }
    }

    private static String embeddingToString(float[] embedding) {
        if (embedding == null || embedding.length == 0) return null;
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(embedding[i]);
        }
        sb.append(']');
        return sb.toString();
    }
}

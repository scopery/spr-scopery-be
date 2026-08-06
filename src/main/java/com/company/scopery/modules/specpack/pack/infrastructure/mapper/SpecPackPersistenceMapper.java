package com.company.scopery.modules.specpack.pack.infrastructure.mapper;

import com.company.scopery.modules.specpack.pack.domain.enums.SpecPackStatus;
import com.company.scopery.modules.specpack.pack.domain.enums.SpecPackType;
import com.company.scopery.modules.specpack.pack.domain.model.SpecPack;
import com.company.scopery.modules.specpack.pack.infrastructure.persistence.SpecPackJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class SpecPackPersistenceMapper {

    public SpecPackJpaEntity toJpaEntity(SpecPack domain) {
        SpecPackJpaEntity entity = new SpecPackJpaEntity();
        entity.setId(domain.id());
        entity.setProjectId(domain.projectId());
        entity.setPackType(domain.packType().name());
        entity.setName(domain.name());
        entity.setDescription(domain.description());
        entity.setStatus(domain.status().name());
        entity.setCurrentVersionId(domain.currentVersionId());
        entity.setSourcePackId(domain.sourcePackId());
        entity.setArchivedAt(domain.archivedAt());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }

    public SpecPack toDomain(SpecPackJpaEntity entity) {
        return SpecPack.reconstitute(
                entity.getId(),
                entity.getProjectId(),
                SpecPackType.valueOf(entity.getPackType()),
                entity.getName(),
                entity.getDescription(),
                SpecPackStatus.valueOf(entity.getStatus()),
                entity.getCurrentVersionId(),
                entity.getSourcePackId(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getArchivedAt()
        );
    }
}

package com.company.scopery.modules.documenthub.template.infrastructure.persistence;

import com.company.scopery.modules.documenthub.template.domain.model.DocumentTemplateVersion;
import com.company.scopery.modules.documenthub.template.domain.model.DocumentTemplateVersionRepository;
import com.company.scopery.modules.documenthub.template.infrastructure.mapper.DocumentTemplateVersionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaDocumentTemplateVersionRepository implements DocumentTemplateVersionRepository {

    private final SpringDataDocumentTemplateVersionJpaRepository springData;
    private final DocumentTemplateVersionPersistenceMapper mapper;

    public JpaDocumentTemplateVersionRepository(SpringDataDocumentTemplateVersionJpaRepository springData,
                                                 DocumentTemplateVersionPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public DocumentTemplateVersion save(DocumentTemplateVersion version) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(version)));
    }

    @Override
    public Optional<DocumentTemplateVersion> findByIdAndTemplateId(UUID id, UUID templateId) {
        return springData.findByIdAndTemplateId(id, templateId).map(mapper::toDomain);
    }
}

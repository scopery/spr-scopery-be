package com.company.scopery.modules.documenthub.nativecontent.infrastructure.persistence;

import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentContent;
import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentContentRepository;
import com.company.scopery.modules.documenthub.nativecontent.infrastructure.mapper.DocumentContentPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaDocumentContentRepository implements DocumentContentRepository {

    private final SpringDataDocumentContentJpaRepository springData;
    private final DocumentContentPersistenceMapper mapper;

    public JpaDocumentContentRepository(SpringDataDocumentContentJpaRepository springData,
                                         DocumentContentPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public DocumentContent save(DocumentContent content) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(content)));
    }

    @Override
    public Optional<DocumentContent> findByDocumentId(UUID documentId) {
        return springData.findByDocumentId(documentId).map(mapper::toDomain);
    }
}

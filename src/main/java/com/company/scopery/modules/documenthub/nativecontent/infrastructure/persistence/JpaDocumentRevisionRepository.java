package com.company.scopery.modules.documenthub.nativecontent.infrastructure.persistence;

import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentRevision;
import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentRevisionRepository;
import com.company.scopery.modules.documenthub.nativecontent.infrastructure.mapper.DocumentRevisionPersistenceMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaDocumentRevisionRepository implements DocumentRevisionRepository {

    private final SpringDataDocumentRevisionJpaRepository springData;
    private final DocumentRevisionPersistenceMapper mapper;

    public JpaDocumentRevisionRepository(SpringDataDocumentRevisionJpaRepository springData,
                                          DocumentRevisionPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public DocumentRevision save(DocumentRevision revision) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(revision)));
    }

    @Override
    public Optional<DocumentRevision> findByDocumentIdAndRevisionNo(UUID documentId, long revisionNo) {
        return springData.findByDocumentIdAndRevisionNo(documentId, revisionNo).map(mapper::toDomain);
    }

    @Override
    public Page<DocumentRevision> findByDocumentId(UUID documentId, Pageable pageable) {
        return springData.findByDocumentIdOrderByRevisionNoDesc(documentId, pageable).map(mapper::toDomain);
    }
}

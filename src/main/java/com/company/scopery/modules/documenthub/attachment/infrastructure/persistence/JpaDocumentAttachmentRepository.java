package com.company.scopery.modules.documenthub.attachment.infrastructure.persistence;

import com.company.scopery.modules.documenthub.attachment.domain.model.DocumentAttachment;
import com.company.scopery.modules.documenthub.attachment.domain.model.DocumentAttachmentRepository;
import com.company.scopery.modules.documenthub.attachment.infrastructure.mapper.DocumentAttachmentPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaDocumentAttachmentRepository implements DocumentAttachmentRepository {

    private final SpringDataDocumentAttachmentJpaRepository springData;
    private final DocumentAttachmentPersistenceMapper mapper;

    public JpaDocumentAttachmentRepository(SpringDataDocumentAttachmentJpaRepository springData,
                                            DocumentAttachmentPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public DocumentAttachment save(DocumentAttachment attachment) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(attachment)));
    }

    @Override
    public Optional<DocumentAttachment> findByIdAndDocumentId(UUID id, UUID documentId) {
        return springData.findByIdAndDocumentId(id, documentId).map(mapper::toDomain);
    }

    @Override
    public List<DocumentAttachment> findByDocumentId(UUID documentId) {
        return springData.findByDocumentIdOrderByCreatedAtAsc(documentId).stream().map(mapper::toDomain).toList();
    }
}

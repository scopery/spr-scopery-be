package com.company.scopery.modules.knowledge.documenttypefield.infrastructure.persistence;

import com.company.scopery.modules.knowledge.documenttypefield.domain.model.DocumentTypeField;
import com.company.scopery.modules.knowledge.documenttypefield.domain.model.DocumentTypeFieldRepository;
import com.company.scopery.modules.knowledge.documenttypefield.infrastructure.mapper.DocumentTypeFieldPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaDocumentTypeFieldRepository implements DocumentTypeFieldRepository {

    private final SpringDataDocumentTypeFieldJpaRepository springDataRepository;
    private final DocumentTypeFieldPersistenceMapper mapper;

    public JpaDocumentTypeFieldRepository(SpringDataDocumentTypeFieldJpaRepository springDataRepository,
                                          DocumentTypeFieldPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public DocumentTypeField save(DocumentTypeField field) {
        DocumentTypeFieldJpaEntity saved = springDataRepository.saveAndFlush(mapper.toJpaEntity(field));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<DocumentTypeField> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<DocumentTypeField> findByDocumentTypeId(UUID documentTypeId) {
        return springDataRepository.findByDocumentTypeIdOrderByDisplayOrderAsc(documentTypeId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByDocumentTypeIdAndFieldKey(UUID documentTypeId, String fieldKey) {
        return springDataRepository.existsByDocumentTypeIdAndFieldKey(documentTypeId, fieldKey);
    }

    @Override
    public void saveAll(List<DocumentTypeField> fields) {
        List<DocumentTypeFieldJpaEntity> entities = fields.stream().map(mapper::toJpaEntity).toList();
        springDataRepository.saveAllAndFlush(entities);
    }
}

package com.company.scopery.modules.documenthub.suggestion.infrastructure.persistence;

import com.company.scopery.modules.documenthub.suggestion.domain.model.DocumentSuggestion;
import com.company.scopery.modules.documenthub.suggestion.domain.model.DocumentSuggestionRepository;
import com.company.scopery.modules.documenthub.suggestion.infrastructure.mapper.DocumentSuggestionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaDocumentSuggestionRepository implements DocumentSuggestionRepository {

    private final SpringDataDocumentSuggestionJpaRepository springData;
    private final DocumentSuggestionPersistenceMapper mapper;

    public JpaDocumentSuggestionRepository(SpringDataDocumentSuggestionJpaRepository springData,
                                            DocumentSuggestionPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public DocumentSuggestion save(DocumentSuggestion suggestion) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(suggestion)));
    }

    @Override
    public Optional<DocumentSuggestion> findByIdAndDocumentId(UUID id, UUID documentId) {
        return springData.findByIdAndDocumentId(id, documentId).map(mapper::toDomain);
    }

    @Override
    public List<DocumentSuggestion> findByDocumentId(UUID documentId) {
        return springData.findByDocumentIdOrderByCreatedAtDesc(documentId).stream().map(mapper::toDomain).toList();
    }
}

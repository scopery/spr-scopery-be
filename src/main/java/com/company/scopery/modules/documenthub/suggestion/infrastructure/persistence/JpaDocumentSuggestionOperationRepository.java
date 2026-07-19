package com.company.scopery.modules.documenthub.suggestion.infrastructure.persistence;

import com.company.scopery.modules.documenthub.suggestion.domain.model.DocumentSuggestionOperation;
import com.company.scopery.modules.documenthub.suggestion.domain.model.DocumentSuggestionOperationRepository;
import com.company.scopery.modules.documenthub.suggestion.infrastructure.mapper.DocumentSuggestionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaDocumentSuggestionOperationRepository implements DocumentSuggestionOperationRepository {

    private final SpringDataDocumentSuggestionOperationJpaRepository springData;
    private final DocumentSuggestionPersistenceMapper mapper;

    public JpaDocumentSuggestionOperationRepository(SpringDataDocumentSuggestionOperationJpaRepository springData,
                                                     DocumentSuggestionPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public void saveAll(List<DocumentSuggestionOperation> operations) {
        springData.saveAllAndFlush(operations.stream().map(mapper::toOperationJpaEntity).toList());
    }

    @Override
    public List<DocumentSuggestionOperation> findBySuggestionId(UUID suggestionId) {
        return springData.findBySuggestionIdOrderByOrdinalAsc(suggestionId).stream()
                .map(mapper::toOperationDomain).toList();
    }
}

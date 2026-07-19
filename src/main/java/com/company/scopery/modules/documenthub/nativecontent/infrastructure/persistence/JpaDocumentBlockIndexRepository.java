package com.company.scopery.modules.documenthub.nativecontent.infrastructure.persistence;

import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentBlockIndex;
import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentBlockIndexRepository;
import com.company.scopery.modules.documenthub.nativecontent.infrastructure.mapper.DocumentBlockIndexPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaDocumentBlockIndexRepository implements DocumentBlockIndexRepository {

    private final SpringDataDocumentBlockIndexJpaRepository springData;
    private final DocumentBlockIndexPersistenceMapper mapper;

    public JpaDocumentBlockIndexRepository(SpringDataDocumentBlockIndexJpaRepository springData,
                                            DocumentBlockIndexPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public void saveAll(List<DocumentBlockIndex> blocks) {
        springData.saveAllAndFlush(blocks.stream().map(mapper::toJpaEntity).toList());
    }

    @Override
    public void deleteByDocumentId(UUID documentId) {
        springData.deleteByDocumentId(documentId);
    }

    @Override
    public List<DocumentBlockIndex> findByDocumentId(UUID documentId) {
        return springData.findByDocumentIdOrderByOrdinalAsc(documentId).stream().map(mapper::toDomain).toList();
    }
}

package com.company.scopery.modules.documenthub.nativecontent.infrastructure.persistence;

import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentRelation;
import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentRelationRepository;
import com.company.scopery.modules.documenthub.nativecontent.infrastructure.mapper.DocumentRelationPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaDocumentRelationRepository implements DocumentRelationRepository {

    private final SpringDataDocumentRelationJpaRepository springData;
    private final DocumentRelationPersistenceMapper mapper;

    public JpaDocumentRelationRepository(SpringDataDocumentRelationJpaRepository springData,
                                          DocumentRelationPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public void saveAll(List<DocumentRelation> relations) {
        springData.saveAllAndFlush(relations.stream().map(mapper::toJpaEntity).toList());
    }

    @Override
    public void deleteBySourceDocumentId(UUID sourceDocumentId) {
        springData.deleteBySourceDocumentId(sourceDocumentId);
    }

    @Override
    public List<DocumentRelation> findBySourceDocumentId(UUID sourceDocumentId) {
        return springData.findBySourceDocumentId(sourceDocumentId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<DocumentRelation> findByTargetDocumentId(UUID targetDocumentId) {
        return springData.findByTargetDocumentId(targetDocumentId).stream().map(mapper::toDomain).toList();
    }
}

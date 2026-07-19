package com.company.scopery.modules.documenthub.nativecontent.infrastructure.persistence;

import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentMention;
import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentMentionRepository;
import com.company.scopery.modules.documenthub.nativecontent.infrastructure.mapper.DocumentMentionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaDocumentMentionRepository implements DocumentMentionRepository {

    private final SpringDataDocumentMentionJpaRepository springData;
    private final DocumentMentionPersistenceMapper mapper;

    public JpaDocumentMentionRepository(SpringDataDocumentMentionJpaRepository springData,
                                         DocumentMentionPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public void saveAll(List<DocumentMention> mentions) {
        springData.saveAllAndFlush(mentions.stream().map(mapper::toJpaEntity).toList());
    }

    @Override
    public void deleteByDocumentId(UUID documentId) {
        springData.deleteByDocumentId(documentId);
    }

    @Override
    public List<DocumentMention> findByDocumentId(UUID documentId) {
        return springData.findByDocumentId(documentId).stream().map(mapper::toDomain).toList();
    }
}

package com.company.scopery.modules.servicesupport.comment.infrastructure.persistence;

import com.company.scopery.modules.servicesupport.comment.domain.model.SupportComment;
import com.company.scopery.modules.servicesupport.comment.domain.model.SupportCommentRepository;
import com.company.scopery.modules.servicesupport.comment.infrastructure.mapper.SupportCommentPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaSupportCommentRepository implements SupportCommentRepository {
    private final SpringDataSupportCommentJpaRepository spring;
    private final SupportCommentPersistenceMapper mapper;
    public JpaSupportCommentRepository(SpringDataSupportCommentJpaRepository spring, SupportCommentPersistenceMapper mapper) {
        this.spring = spring; this.mapper = mapper;
    }
    @Override public SupportComment save(SupportComment c) { return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(c))); }
    @Override public List<SupportComment> findBySupportCaseId(UUID caseId) {
        return spring.findBySupportCaseIdOrderByCreatedAtAsc(caseId).stream().map(mapper::toDomain).toList();
    }
}

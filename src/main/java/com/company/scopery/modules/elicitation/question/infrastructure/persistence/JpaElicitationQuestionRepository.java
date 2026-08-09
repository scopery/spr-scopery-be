package com.company.scopery.modules.elicitation.question.infrastructure.persistence;

import com.company.scopery.modules.elicitation.question.domain.model.ElicitationQuestion;
import com.company.scopery.modules.elicitation.question.domain.model.ElicitationQuestionRepository;
import com.company.scopery.modules.elicitation.question.infrastructure.mapper.ElicitationQuestionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaElicitationQuestionRepository implements ElicitationQuestionRepository {

    private final SpringDataElicitationQuestionJpaRepository springDataRepo;
    private final ElicitationQuestionPersistenceMapper mapper;

    public JpaElicitationQuestionRepository(SpringDataElicitationQuestionJpaRepository springDataRepo,
                                             ElicitationQuestionPersistenceMapper mapper) {
        this.springDataRepo = springDataRepo;
        this.mapper = mapper;
    }

    @Override
    public ElicitationQuestion save(ElicitationQuestion question) {
        ElicitationQuestionJpaEntity entity = mapper.toJpaEntity(question);
        ElicitationQuestionJpaEntity saved = springDataRepo.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<ElicitationQuestion> saveAll(List<ElicitationQuestion> questions) {
        List<ElicitationQuestionJpaEntity> entities = questions.stream().map(mapper::toJpaEntity).toList();
        return springDataRepo.saveAllAndFlush(entities).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<ElicitationQuestion> findById(UUID id) {
        return springDataRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ElicitationQuestion> findAllBySessionId(UUID sessionId) {
        return springDataRepo.findBySessionIdOrderBySequenceAsc(sessionId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public int countBySessionId(UUID sessionId) {
        return springDataRepo.countBySessionId(sessionId);
    }

    @Override
    public int findMaxSequenceBySessionId(UUID sessionId) {
        return springDataRepo.findMaxSequenceBySessionId(sessionId);
    }
}

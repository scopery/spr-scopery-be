package com.company.scopery.modules.knowledge.retrieval.infrastructure.persistence;

import com.company.scopery.modules.knowledge.retrieval.domain.model.RetrievalTrace;
import com.company.scopery.modules.knowledge.retrieval.domain.model.RetrievalTraceRepository;
import com.company.scopery.modules.knowledge.retrieval.infrastructure.mapper.RetrievalTracePersistenceMapper;
import org.springframework.stereotype.Repository;

@Repository
public class JpaRetrievalTraceRepository implements RetrievalTraceRepository {

    private final SpringDataRetrievalTraceJpaRepository springData;
    private final RetrievalTracePersistenceMapper mapper;

    public JpaRetrievalTraceRepository(SpringDataRetrievalTraceJpaRepository springData,
                                        RetrievalTracePersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public RetrievalTrace save(RetrievalTrace trace) {
        RetrievalTraceJpaEntity entity = mapper.toJpaEntity(trace);
        RetrievalTraceJpaEntity saved = springData.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }
}

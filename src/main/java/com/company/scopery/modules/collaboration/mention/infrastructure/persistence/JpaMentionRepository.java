package com.company.scopery.modules.collaboration.mention.infrastructure.persistence;
import com.company.scopery.modules.collaboration.mention.domain.enums.MentionSourceType;
import com.company.scopery.modules.collaboration.mention.domain.model.*;
import com.company.scopery.modules.collaboration.mention.infrastructure.mapper.MentionPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaMentionRepository implements MentionRepository {
    private final SpringDataMentionJpaRepository springData; private final MentionPersistenceMapper mapper;
    public JpaMentionRepository(SpringDataMentionJpaRepository springData, MentionPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public Mention save(Mention m) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(m))); }
    @Override public List<Mention> findBySource(MentionSourceType sourceType, UUID sourceId) {
        return springData.findBySourceTypeAndSourceId(sourceType.name(), sourceId).stream().map(mapper::toDomain).toList();
    }
}

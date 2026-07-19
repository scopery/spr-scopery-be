package com.company.scopery.modules.resourcereference.resourcetype.infrastructure.persistence;

import com.company.scopery.modules.resourcereference.resourcetype.domain.model.MentionResourceTypeDefinition;
import com.company.scopery.modules.resourcereference.resourcetype.domain.model.MentionResourceTypeRepository;
import com.company.scopery.modules.resourcereference.resourcetype.infrastructure.mapper.MentionResourceTypePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaMentionResourceTypeRepository implements MentionResourceTypeRepository {

    private final SpringDataMentionResourceTypeJpaRepository springData;
    private final MentionResourceTypePersistenceMapper mapper;

    public JpaMentionResourceTypeRepository(SpringDataMentionResourceTypeJpaRepository springData,
                                             MentionResourceTypePersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public MentionResourceTypeDefinition save(MentionResourceTypeDefinition type) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(type)));
    }

    @Override
    public Optional<MentionResourceTypeDefinition> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<MentionResourceTypeDefinition> findByCode(String code) {
        return springData.findByCode(code).map(mapper::toDomain);
    }

    @Override
    public boolean existsByCode(String code) {
        return springData.existsByCode(code);
    }

    @Override
    public List<MentionResourceTypeDefinition> findAll() {
        return springData.findAllByOrderByCodeAsc().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<MentionResourceTypeDefinition> findAllEnabled() {
        return springData.findByEnabledTrueOrderByCodeAsc().stream().map(mapper::toDomain).toList();
    }
}

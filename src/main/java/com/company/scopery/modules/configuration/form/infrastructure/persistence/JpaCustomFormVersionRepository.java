package com.company.scopery.modules.configuration.form.infrastructure.persistence;
import com.company.scopery.modules.configuration.form.domain.model.*;
import com.company.scopery.modules.configuration.form.infrastructure.mapper.FormPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaCustomFormVersionRepository implements CustomFormVersionRepository {
    private final SpringDataCustomFormVersionJpaRepository springData; private final FormPersistenceMapper mapper;
    public JpaCustomFormVersionRepository(SpringDataCustomFormVersionJpaRepository springData, FormPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public CustomFormVersion save(CustomFormVersion v) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(v))); }
    @Override public Optional<CustomFormVersion> findById(UUID id) { return springData.findById(id).map(mapper::toDomain); }
    @Override public List<CustomFormVersion> findByFormId(UUID formId) { return springData.findByFormDefinitionIdOrderByVersionNumberDesc(formId).stream().map(mapper::toDomain).toList(); }
    @Override public int nextVersionNumber(UUID formId) { return springData.maxVersionNumber(formId) + 1; }
}

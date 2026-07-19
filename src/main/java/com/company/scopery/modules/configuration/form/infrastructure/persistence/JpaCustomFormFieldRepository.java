package com.company.scopery.modules.configuration.form.infrastructure.persistence;
import com.company.scopery.modules.configuration.form.domain.model.*;
import com.company.scopery.modules.configuration.form.infrastructure.mapper.FormPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaCustomFormFieldRepository implements CustomFormFieldRepository {
    private final SpringDataCustomFormFieldJpaRepository springData; private final FormPersistenceMapper mapper;
    public JpaCustomFormFieldRepository(SpringDataCustomFormFieldJpaRepository springData, FormPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public CustomFormField save(CustomFormField f) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(f))); }
    @Override public Optional<CustomFormField> findById(UUID id) { return springData.findById(id).map(mapper::toDomain); }
    @Override public List<CustomFormField> findByVersionId(UUID versionId) { return springData.findByFormVersionIdOrderBySortOrderAsc(versionId).stream().map(mapper::toDomain).toList(); }
    @Override public void delete(UUID id) { springData.deleteById(id); }
}

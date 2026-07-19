package com.company.scopery.modules.configuration.form.infrastructure.persistence;
import com.company.scopery.modules.configuration.form.domain.model.*;
import com.company.scopery.modules.configuration.form.infrastructure.mapper.FormPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaCustomFormSectionRepository implements CustomFormSectionRepository {
    private final SpringDataCustomFormSectionJpaRepository springData; private final FormPersistenceMapper mapper;
    public JpaCustomFormSectionRepository(SpringDataCustomFormSectionJpaRepository springData, FormPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public CustomFormSection save(CustomFormSection s) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(s))); }
    @Override public Optional<CustomFormSection> findById(UUID id) { return springData.findById(id).map(mapper::toDomain); }
    @Override public List<CustomFormSection> findByVersionId(UUID versionId) { return springData.findByFormVersionIdOrderBySortOrderAsc(versionId).stream().map(mapper::toDomain).toList(); }
    @Override public void delete(UUID id) { springData.deleteById(id); }
}

package com.company.scopery.modules.configuration.form.infrastructure.persistence;
import com.company.scopery.modules.configuration.form.domain.model.*;
import com.company.scopery.modules.configuration.form.infrastructure.mapper.FormPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaFormSubmissionRepository implements FormSubmissionRepository {
    private final SpringDataFormSubmissionJpaRepository springData; private final FormPersistenceMapper mapper;
    public JpaFormSubmissionRepository(SpringDataFormSubmissionJpaRepository springData, FormPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public FormSubmission save(FormSubmission s) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(s))); }
    @Override public Optional<FormSubmission> findByIdAndWorkspaceId(UUID id, UUID workspaceId) { return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain); }
    @Override public List<FormSubmission> findByWorkspaceId(UUID workspaceId) { return springData.findByWorkspaceIdOrderByCreatedAtDesc(workspaceId).stream().map(mapper::toDomain).toList(); }
}

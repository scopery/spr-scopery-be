package com.company.scopery.modules.documenthub.template.infrastructure.persistence;

import com.company.scopery.modules.documenthub.template.domain.model.DocumentTemplateVariable;
import com.company.scopery.modules.documenthub.template.domain.model.DocumentTemplateVariableRepository;
import com.company.scopery.modules.documenthub.template.infrastructure.mapper.DocumentTemplateVariablePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaDocumentTemplateVariableRepository implements DocumentTemplateVariableRepository {

    private final SpringDataDocumentTemplateVariableJpaRepository springData;
    private final DocumentTemplateVariablePersistenceMapper mapper;

    public JpaDocumentTemplateVariableRepository(SpringDataDocumentTemplateVariableJpaRepository springData,
                                                  DocumentTemplateVariablePersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public DocumentTemplateVariable save(DocumentTemplateVariable variable) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(variable)));
    }

    @Override
    public List<DocumentTemplateVariable> saveAll(List<DocumentTemplateVariable> variables) {
        var entities = variables.stream().map(mapper::toJpaEntity).toList();
        return springData.saveAllAndFlush(entities).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<DocumentTemplateVariable> findByTemplateVersionId(UUID templateVersionId) {
        return springData.findByTemplateVersionIdOrderByOrdinalAsc(templateVersionId).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public void deleteByTemplateVersionId(UUID templateVersionId) {
        springData.deleteByTemplateVersionId(templateVersionId);
    }
}

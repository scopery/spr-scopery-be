package com.company.scopery.modules.project.templatephase.infrastructure.persistence;

import com.company.scopery.modules.project.templatephase.domain.model.ProjectTemplatePhase;
import com.company.scopery.modules.project.templatephase.domain.model.ProjectTemplatePhaseRepository;
import com.company.scopery.modules.project.templatephase.infrastructure.mapper.ProjectTemplatePhasePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProjectTemplatePhaseRepository implements ProjectTemplatePhaseRepository {

    private final SpringDataProjectTemplatePhaseJpaRepository springDataRepository;
    private final ProjectTemplatePhasePersistenceMapper mapper;

    public JpaProjectTemplatePhaseRepository(SpringDataProjectTemplatePhaseJpaRepository springDataRepository,
                                             ProjectTemplatePhasePersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public ProjectTemplatePhase save(ProjectTemplatePhase phase) {
        return mapper.toDomain(springDataRepository.saveAndFlush(mapper.toJpaEntity(phase)));
    }

    @Override
    public Optional<ProjectTemplatePhase> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        springDataRepository.deleteById(id);
        springDataRepository.flush();
    }

    @Override
    public List<ProjectTemplatePhase> findByTemplateVersionId(UUID templateVersionId) {
        return springDataRepository.findByTemplateVersionIdOrderByDisplayOrderAsc(templateVersionId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByPhaseDefinitionId(UUID phaseDefinitionId) {
        return springDataRepository.existsByPhaseDefinitionId(phaseDefinitionId);
    }

    @Override
    public boolean existsByTemplateVersionIdAndDisplayOrder(UUID templateVersionId, int displayOrder) {
        return springDataRepository.existsByTemplateVersionIdAndDisplayOrder(templateVersionId, displayOrder);
    }
}

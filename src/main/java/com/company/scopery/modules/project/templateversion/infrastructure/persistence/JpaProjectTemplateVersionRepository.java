package com.company.scopery.modules.project.templateversion.infrastructure.persistence;

import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersion;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersionRepository;
import com.company.scopery.modules.project.templateversion.infrastructure.mapper.ProjectTemplateVersionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProjectTemplateVersionRepository implements ProjectTemplateVersionRepository {

    private final SpringDataProjectTemplateVersionJpaRepository springDataRepository;
    private final ProjectTemplateVersionPersistenceMapper mapper;

    public JpaProjectTemplateVersionRepository(SpringDataProjectTemplateVersionJpaRepository springDataRepository,
                                               ProjectTemplateVersionPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public ProjectTemplateVersion save(ProjectTemplateVersion version) {
        return mapper.toDomain(springDataRepository.saveAndFlush(mapper.toJpaEntity(version)));
    }

    @Override
    public Optional<ProjectTemplateVersion> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ProjectTemplateVersion> findByTemplateId(UUID templateId) {
        return springDataRepository.findByProjectTemplateIdOrderByVersionNumberAsc(templateId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Integer> findMaxVersionNumber(UUID templateId) {
        return springDataRepository.findMaxVersionNumber(templateId);
    }
}

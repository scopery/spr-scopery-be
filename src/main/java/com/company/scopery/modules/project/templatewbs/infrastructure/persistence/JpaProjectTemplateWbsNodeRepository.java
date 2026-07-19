package com.company.scopery.modules.project.templatewbs.infrastructure.persistence;

import com.company.scopery.modules.project.templatewbs.domain.model.ProjectTemplateWbsNode;
import com.company.scopery.modules.project.templatewbs.domain.model.ProjectTemplateWbsNodeRepository;
import com.company.scopery.modules.project.templatewbs.infrastructure.mapper.ProjectTemplateWbsNodePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProjectTemplateWbsNodeRepository implements ProjectTemplateWbsNodeRepository {

    private final SpringDataProjectTemplateWbsNodeJpaRepository springDataRepository;
    private final ProjectTemplateWbsNodePersistenceMapper mapper;

    public JpaProjectTemplateWbsNodeRepository(SpringDataProjectTemplateWbsNodeJpaRepository springDataRepository,
                                               ProjectTemplateWbsNodePersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public ProjectTemplateWbsNode save(ProjectTemplateWbsNode node) {
        return mapper.toDomain(springDataRepository.saveAndFlush(mapper.toJpaEntity(node)));
    }

    @Override
    public Optional<ProjectTemplateWbsNode> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        springDataRepository.deleteById(id);
        springDataRepository.flush();
    }

    @Override
    public List<ProjectTemplateWbsNode> findByTemplateVersionId(UUID templateVersionId) {
        return springDataRepository.findByTemplateVersionIdOrderByDepthAscOrderIndexAsc(templateVersionId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ProjectTemplateWbsNode> findChildrenByParentId(UUID parentId) {
        return springDataRepository.findByParentIdOrderByOrderIndexAsc(parentId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByTemplatePhaseId(UUID templatePhaseId) {
        return springDataRepository.existsByTemplatePhaseId(templatePhaseId);
    }
}

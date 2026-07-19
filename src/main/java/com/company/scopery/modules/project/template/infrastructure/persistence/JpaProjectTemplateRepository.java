package com.company.scopery.modules.project.template.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.project.template.domain.enums.ProjectTemplateCategory;
import com.company.scopery.modules.project.template.domain.enums.ProjectTemplateScope;
import com.company.scopery.modules.project.template.domain.enums.ProjectTemplateStatus;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplateRepository;
import com.company.scopery.modules.project.template.infrastructure.mapper.ProjectTemplatePersistenceMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProjectTemplateRepository implements ProjectTemplateRepository {

    private final SpringDataProjectTemplateJpaRepository springDataRepository;
    private final ProjectTemplatePersistenceMapper mapper;

    public JpaProjectTemplateRepository(SpringDataProjectTemplateJpaRepository springDataRepository,
                                        ProjectTemplatePersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public ProjectTemplate save(ProjectTemplate template) {
        ProjectTemplateJpaEntity entity = mapper.toJpaEntity(template);
        return mapper.toDomain(springDataRepository.saveAndFlush(entity));
    }

    @Override
    public Optional<ProjectTemplate> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByCodeAndScope(String code, ProjectTemplateScope scope) {
        return springDataRepository.existsByCodeAndScope(code, scope.name());
    }

    @Override
    public boolean existsByCodeAndScopeAndWorkspaceId(String code, ProjectTemplateScope scope, UUID workspaceId) {
        return springDataRepository.existsByCodeAndScopeAndWorkspaceId(code, scope.name(), workspaceId);
    }

    @Override
    public boolean existsByCodeAndScopeAndOrganizationId(String code, ProjectTemplateScope scope, UUID organizationId) {
        return springDataRepository.existsByCodeAndScopeAndOrganizationId(code, scope.name(), organizationId);
    }

    @Override
    public PageResult<ProjectTemplate> search(
            ProjectTemplateScope scope,
            UUID workspaceId,
            UUID organizationId,
            ProjectTemplateStatus status,
            ProjectTemplateCategory category,
            String keyword,
            PageQuery pageQuery) {

        Specification<ProjectTemplateJpaEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (scope != null) {
                predicates.add(cb.equal(root.get("scope"), scope.name()));
            }
            if (workspaceId != null) {
                predicates.add(cb.equal(root.get("workspaceId"), workspaceId));
            }
            if (organizationId != null) {
                predicates.add(cb.equal(root.get("organizationId"), organizationId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            if (category != null) {
                predicates.add(cb.equal(root.get("category"), category.name()));
            }
            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("code")), pattern)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        Pageable pageable = PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
        Page<ProjectTemplate> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }
}

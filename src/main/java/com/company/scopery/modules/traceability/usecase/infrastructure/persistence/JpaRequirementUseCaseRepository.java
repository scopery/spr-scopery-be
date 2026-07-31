package com.company.scopery.modules.traceability.usecase.infrastructure.persistence;

import com.company.scopery.modules.traceability.usecase.domain.model.RequirementUseCaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaRequirementUseCaseRepository implements RequirementUseCaseRepository {

    private final SpringDataRequirementUseCaseJpaRepository springData;

    public JpaRequirementUseCaseRepository(SpringDataRequirementUseCaseJpaRepository springData) {
        this.springData = springData;
    }

    @Override
    public void link(UUID requirementId, UUID useCaseId) {
        if (!exists(requirementId, useCaseId)) {
            springData.saveAndFlush(new RequirementUseCaseJpaEntity(new RequirementUseCaseId(requirementId, useCaseId)));
        }
    }

    @Override
    public void unlink(UUID requirementId, UUID useCaseId) {
        springData.deleteById(new RequirementUseCaseId(requirementId, useCaseId));
    }

    @Override
    public boolean exists(UUID requirementId, UUID useCaseId) {
        return springData.existsById(new RequirementUseCaseId(requirementId, useCaseId));
    }

    @Override
    public List<UUID> findUseCaseIdsByRequirementId(UUID requirementId) {
        return springData.findUseCaseIdsByRequirementId(requirementId);
    }

    @Override
    public List<UUID> findRequirementIdsByUseCaseId(UUID useCaseId) {
        return springData.findRequirementIdsByUseCaseId(useCaseId);
    }
}

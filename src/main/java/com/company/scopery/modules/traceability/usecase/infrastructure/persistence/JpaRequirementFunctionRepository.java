package com.company.scopery.modules.traceability.usecase.infrastructure.persistence;

import com.company.scopery.modules.traceability.usecase.domain.model.RequirementFunctionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaRequirementFunctionRepository implements RequirementFunctionRepository {

    private final SpringDataRequirementFunctionJpaRepository springData;

    public JpaRequirementFunctionRepository(SpringDataRequirementFunctionJpaRepository springData) {
        this.springData = springData;
    }

    @Override
    public void link(UUID requirementId, UUID functionId) {
        if (!exists(requirementId, functionId)) {
            springData.saveAndFlush(new RequirementFunctionJpaEntity(new RequirementFunctionId(requirementId, functionId)));
        }
    }

    @Override
    public void unlink(UUID requirementId, UUID functionId) {
        springData.deleteById(new RequirementFunctionId(requirementId, functionId));
    }

    @Override
    public boolean exists(UUID requirementId, UUID functionId) {
        return springData.existsById(new RequirementFunctionId(requirementId, functionId));
    }

    @Override
    public List<UUID> findFunctionIdsByRequirementId(UUID requirementId) {
        return springData.findFunctionIdsByRequirementId(requirementId);
    }

    @Override
    public List<UUID> findRequirementIdsByFunctionId(UUID functionId) {
        return springData.findRequirementIdsByFunctionId(functionId);
    }

    @Override
    public boolean existsByRequirementId(UUID requirementId) {
        return springData.existsByRequirementId(requirementId);
    }
}

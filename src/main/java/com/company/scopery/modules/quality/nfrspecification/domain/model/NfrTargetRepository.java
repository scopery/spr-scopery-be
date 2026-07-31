package com.company.scopery.modules.quality.nfrspecification.domain.model;
import java.util.List; import java.util.UUID;
public interface NfrTargetRepository {
    List<NfrTarget> findByRequirementId(UUID requirementId);
    List<NfrTarget> saveAll(List<NfrTarget> targets);
    void deleteByRequirementId(UUID requirementId);
}

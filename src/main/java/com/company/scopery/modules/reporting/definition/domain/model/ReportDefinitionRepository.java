package com.company.scopery.modules.reporting.definition.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface ReportDefinitionRepository {
    ReportDefinition save(ReportDefinition definition);
    Optional<ReportDefinition> findById(UUID id);
    Optional<ReportDefinition> findByCode(String code);
    List<ReportDefinition> findAllActive();
}

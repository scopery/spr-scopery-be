package com.company.scopery.modules.reporting.definition.application.service;

import com.company.scopery.modules.reporting.definition.application.response.ReportDefinitionResponse;
import com.company.scopery.modules.reporting.definition.domain.model.ReportDefinition;
import com.company.scopery.modules.reporting.definition.domain.model.ReportDefinitionRepository;
import com.company.scopery.modules.reporting.shared.error.ReportingExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReportDefinitionQueryService {
    private final ReportDefinitionRepository definitions;

    public ReportDefinitionQueryService(ReportDefinitionRepository definitions) {
        this.definitions = definitions;
    }

    @Transactional(readOnly = true)
    public List<ReportDefinitionResponse> list() {
        return definitions.findAllActive().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ReportDefinitionResponse getByCode(String code) {
        return definitions.findByCode(code).map(this::toResponse)
                .orElseThrow(() -> ReportingExceptions.definitionNotFound(code));
    }

    private ReportDefinitionResponse toResponse(ReportDefinition d) {
        return new ReportDefinitionResponse(
                d.id(), d.code(), d.name(), d.reportType(), d.scope(), d.status().name(), d.supportedFormatsJson());
    }
}

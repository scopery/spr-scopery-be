package com.company.scopery.modules.integrationhub.provider.application.service;
import com.company.scopery.modules.integrationhub.provider.application.response.ConnectorCapabilityResponse;
import com.company.scopery.modules.integrationhub.provider.application.response.IntegrationProviderResponse;
import com.company.scopery.modules.integrationhub.provider.domain.model.ConnectorCapabilityRepository;
import com.company.scopery.modules.integrationhub.provider.domain.model.IntegrationProviderRepository;
import com.company.scopery.modules.integrationhub.shared.error.IntegrationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
public class IntegrationProviderQueryService {
    private final IntegrationProviderRepository providers;
    private final ConnectorCapabilityRepository capabilities;
    public IntegrationProviderQueryService(IntegrationProviderRepository providers, ConnectorCapabilityRepository capabilities) {
        this.providers = providers; this.capabilities = capabilities;
    }
    @Transactional(readOnly = true)
    public List<IntegrationProviderResponse> listAll() {
        return providers.findAll().stream().map(IntegrationProviderResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public IntegrationProviderResponse getByCode(String code) {
        return providers.findByCode(code).map(IntegrationProviderResponse::from)
                .orElseThrow(() -> IntegrationExceptions.providerNotFound(code));
    }
    @Transactional(readOnly = true)
    public List<ConnectorCapabilityResponse> listCapabilities(String providerCode) {
        return capabilities.findByProviderCode(providerCode).stream().map(ConnectorCapabilityResponse::from).toList();
    }
}

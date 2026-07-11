package com.company.scopery.integration.ai;

import com.company.scopery.common.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AiProviderAdapterRegistry {

    private final Map<String, AiProviderAdapter> adapters;

    public AiProviderAdapterRegistry(List<AiProviderAdapter> adapterList) {
        this.adapters = adapterList.stream()
                .collect(Collectors.toMap(AiProviderAdapter::supportedProviderCode, a -> a));
    }

    public AiProviderAdapter getAdapter(String providerCode) {
        AiProviderAdapter adapter = adapters.get(providerCode);
        if (adapter == null) {
            throw new BusinessException("Provider adapter not implemented for: " + providerCode);
        }
        return adapter;
    }
}
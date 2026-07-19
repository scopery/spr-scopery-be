package com.company.scopery.integration.ai;

import com.company.scopery.common.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AiStreamingProviderAdapterRegistry {

    private final Map<String, AiStreamingProviderPort> adapters;

    public AiStreamingProviderAdapterRegistry(List<AiStreamingProviderPort> adapterList) {
        this.adapters = adapterList.stream()
                .collect(Collectors.toMap(AiStreamingProviderPort::supportedProviderCode, a -> a));
    }

    public AiStreamingProviderPort getAdapter(String providerCode) {
        AiStreamingProviderPort adapter = adapters.get(providerCode);
        if (adapter == null) {
            throw new BusinessException("Streaming provider adapter not implemented for: " + providerCode);
        }
        return adapter;
    }
}

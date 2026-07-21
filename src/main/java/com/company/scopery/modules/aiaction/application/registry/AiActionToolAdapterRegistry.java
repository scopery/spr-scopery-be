package com.company.scopery.modules.aiaction.application.registry;

import com.company.scopery.modules.aiaction.application.port.AiActionToolAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Collects all AiActionToolAdapter beans — injected into the real ToolRegistryPort implementation (Step 13)
@Component
public class AiActionToolAdapterRegistry {

    private static final Logger log = LoggerFactory.getLogger(AiActionToolAdapterRegistry.class);

    private final Map<String, AiActionToolAdapter> adapters;

    public AiActionToolAdapterRegistry(List<AiActionToolAdapter> adapterList) {
        this.adapters = adapterList.stream()
                .collect(Collectors.toMap(
                        a -> a.toolCode() + ":" + a.toolVersion(),
                        a -> a,
                        (a, b) -> {
                            throw new IllegalStateException(
                                    "Duplicate AiActionToolAdapter for: " + a.toolCode() + " v" + a.toolVersion());
                        }));
        log.info("AiActionToolAdapterRegistry: {} adapters registered", adapters.size());
    }

    public AiActionToolAdapter getAdapter(String toolCode, String toolVersion) {
        return adapters.get(toolCode + ":" + toolVersion);
    }

    public boolean hasAdapter(String toolCode, String toolVersion) {
        return adapters.containsKey(toolCode + ":" + toolVersion);
    }

    public int size() {
        return adapters.size();
    }
}

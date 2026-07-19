package com.company.scopery.modules.airecommendation.shared.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "scopery.ai.recommendation")
public class AiRecommendationProperties {

    private boolean llmEnrichmentEnabled = false;
    private boolean phase21ReadCompatEnabled = true;
    private boolean phase21WriteBridgeEnabled = false;
    private boolean phase21LegacyApplyVisible = true;

    public boolean isLlmEnrichmentEnabled() { return llmEnrichmentEnabled; }
    public void setLlmEnrichmentEnabled(boolean v) { this.llmEnrichmentEnabled = v; }

    public boolean isPhase21ReadCompatEnabled() { return phase21ReadCompatEnabled; }
    public void setPhase21ReadCompatEnabled(boolean v) { this.phase21ReadCompatEnabled = v; }

    public boolean isPhase21WriteBridgeEnabled() { return phase21WriteBridgeEnabled; }
    public void setPhase21WriteBridgeEnabled(boolean v) { this.phase21WriteBridgeEnabled = v; }

    public boolean isPhase21LegacyApplyVisible() { return phase21LegacyApplyVisible; }
    public void setPhase21LegacyApplyVisible(boolean v) { this.phase21LegacyApplyVisible = v; }
}

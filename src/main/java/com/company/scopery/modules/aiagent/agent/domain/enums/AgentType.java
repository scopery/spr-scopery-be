package com.company.scopery.modules.aiagent.agent.domain.enums;

public enum AgentType {
    EXTRACTION,
    CLASSIFICATION,
    SUMMARIZATION,
    GENERATION,
    VALIDATION,
    RECOMMENDATION,
    /** Internal/system-owned agents (e.g. seed prompt owners). Not for normal UI create. */
    SYSTEM,
    OTHER
}

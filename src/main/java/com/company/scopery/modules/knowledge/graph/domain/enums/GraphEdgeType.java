package com.company.scopery.modules.knowledge.graph.domain.enums;

public enum GraphEdgeType {
    PROJECT_HAS_TASK,
    PROJECT_HAS_DOCUMENT_VERSION,
    PROJECT_HAS_MEETING_MINUTE,
    TASK_DEPENDS_ON_TASK,
    MEETING_MINUTE_REFERENCES_TASK,
    DOCUMENT_VERSION_REFERENCES_TASK
}

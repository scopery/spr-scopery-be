package com.company.scopery.modules.knowledge.documenttypefield.domain.enums;

public enum DocumentTypeFieldDataType {
    TEXT,
    LONG_TEXT,
    NUMBER,
    BOOLEAN,
    DATE,
    DATETIME,
    SELECT,
    MULTI_SELECT,
    USER,
    TEAM,
    PROJECT,
    APPLICATION,
    URL,
    EMAIL,
    CURRENCY,
    PERCENT;

    public boolean requiresOptions() {
        return this == SELECT || this == MULTI_SELECT;
    }
}

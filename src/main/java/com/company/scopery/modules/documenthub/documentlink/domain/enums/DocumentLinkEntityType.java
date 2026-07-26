package com.company.scopery.modules.documenthub.documentlink.domain.enums;

public enum DocumentLinkEntityType {
    SESSION,
    ANSWER,
    REQUIREMENT,
    TRACE_ITEM,
    OTHER;

    public static DocumentLinkEntityType fromString(String value) {
        if (value == null) return OTHER;
        for (DocumentLinkEntityType t : values()) {
            if (t.name().equalsIgnoreCase(value)) return t;
        }
        return OTHER;
    }
}

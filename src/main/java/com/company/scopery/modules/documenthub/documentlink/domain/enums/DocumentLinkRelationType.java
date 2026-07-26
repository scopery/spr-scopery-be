package com.company.scopery.modules.documenthub.documentlink.domain.enums;

public enum DocumentLinkRelationType {
    EVIDENCE_FOR,
    SUMMARY_OF,
    GENERATED_FROM,
    RELATED_TO,
    SOURCE_FOR,
    HANDOFF_FOR,
    SUPPORTS,
    DERIVED_FROM,
    OTHER;

    public static DocumentLinkRelationType fromString(String value) {
        if (value == null) return OTHER;
        for (DocumentLinkRelationType t : values()) {
            if (t.name().equalsIgnoreCase(value)) return t;
        }
        return OTHER;
    }
}

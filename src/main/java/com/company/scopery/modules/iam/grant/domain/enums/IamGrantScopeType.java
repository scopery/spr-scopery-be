package com.company.scopery.modules.iam.grant.domain.enums;

public enum IamGrantScopeType {
    RESOURCE, WORKSPACE, TEAM, SPACE, DOCUMENT_TYPE, OWN_CREATED, CLASSIFICATION, CUSTOM,
    /** Grant applies to every resource — used exclusively for system-wide roles (e.g. SUPER_ADMIN). */
    GLOBAL_RESOURCE
}

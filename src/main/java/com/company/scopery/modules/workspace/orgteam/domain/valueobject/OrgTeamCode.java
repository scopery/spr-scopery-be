package com.company.scopery.modules.workspace.orgteam.domain.valueobject;

public record OrgTeamCode(String value) {

    public OrgTeamCode {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("OrgTeamCode must not be blank");
        if (value.length() > 100) throw new IllegalArgumentException("OrgTeamCode must be 100 characters or less");
    }

    public static OrgTeamCode of(String value) {
        return new OrgTeamCode(value.trim().toUpperCase());
    }
}

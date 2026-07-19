package com.company.scopery.modules.externalparty.shared.constant;
import com.company.scopery.common.constant.ApiPaths;
public final class ExternalPartyApiPaths {
    private static final String WBASE = ApiPaths.BASE_PATH + "/workspaces/{workspaceId}";
    public static final String ORGANIZATIONS = WBASE + "/external-organizations";
    public static final String CONTACTS = WBASE + "/external-contacts";
    public static final String ORGANIZATION_ADDRESSES = ORGANIZATIONS + "/{organizationId}/addresses";
    public static final String CONTACT_ADDRESSES = CONTACTS + "/{contactId}/addresses";
    public static final String CONTACT_CHANNELS = CONTACTS + "/{contactId}/channels";
    public static final String CONTACT_PREFERENCE = CONTACTS + "/{contactId}/communication-preference";
    public static final String ORGANIZATION_PREFERENCE = ORGANIZATIONS + "/{organizationId}/communication-preference";
    public static final String ORGANIZATION_DOC_LINKS = ORGANIZATIONS + "/{organizationId}/document-links";
    public static final String CONTACT_DOC_LINKS = CONTACTS + "/{contactId}/document-links";
    private static final String PBASE = ApiPaths.BASE_PATH + "/projects/{projectId}";
    public static final String STAKEHOLDERS = PBASE + "/stakeholders";
    public static final String RELATIONSHIPS = PBASE + "/external-relationships";
    public static final String APPROVAL_AUTHORITIES = PBASE + "/approval-authorities";
    public static final String REPORTS = PBASE + "/reports";
    private ExternalPartyApiPaths() {}
}

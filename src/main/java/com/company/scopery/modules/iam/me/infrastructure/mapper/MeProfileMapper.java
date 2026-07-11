package com.company.scopery.modules.iam.me.infrastructure.mapper;

import com.company.scopery.modules.iam.me.domain.model.MeProfile;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.workspace.orgmember.application.response.OrgMembershipSummaryResponse;

import java.util.List;

public final class MeProfileMapper {

    private MeProfileMapper() {}

    public static MeProfile toProfile(IamUser user, List<OrgMembershipSummaryResponse> memberships) {
        List<MeProfile.OrganizationMembership> organizationMemberships = memberships.stream()
                .map(m -> new MeProfile.OrganizationMembership(
                        m.organizationId(), m.organizationName(), m.membershipType(), m.status()))
                .toList();
        return MeProfile.of(user, organizationMemberships);
    }
}

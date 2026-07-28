package com.company.scopery.modules.workspace.orgmember.application.service;

import com.company.scopery.modules.workspace.orgmember.domain.enums.OrgMemberStatus;
import com.company.scopery.modules.workspace.orgmember.domain.enums.OrgMembershipSource;
import com.company.scopery.modules.workspace.orgmember.domain.enums.OrgMembershipType;
import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMember;
import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMemberRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;

/**
 * Create or reinstate organization membership (kick → re-invite / re-add).
 */
@Service
public class OrgMembershipEnrollmentService {

    private final OrgMemberRepository orgMemberRepository;

    public OrgMembershipEnrollmentService(OrgMemberRepository orgMemberRepository) {
        this.orgMemberRepository = orgMemberRepository;
    }

    public OrgMember ensureActiveMembership(
            UUID organizationId,
            UUID userId,
            OrgMembershipType membershipType,
            OrgMembershipSource source,
            BiFunction<UUID, UUID, RuntimeException> alreadyActiveError) {
        Optional<OrgMember> existing = orgMemberRepository.findByOrganizationIdAndUserId(organizationId, userId);
        if (existing.isEmpty()) {
            return orgMemberRepository.save(
                    OrgMember.create(organizationId, userId, membershipType, source));
        }
        OrgMember member = existing.get();
        if (member.status() == OrgMemberStatus.ACTIVE) {
            throw alreadyActiveError.apply(organizationId, userId);
        }
        return orgMemberRepository.save(member.reinstate(membershipType, source));
    }

    /** Soft ensure: reinstate if non-active; no-op if already active; create if missing. */
    public OrgMember ensureActiveMembershipQuiet(
            UUID organizationId,
            UUID userId,
            OrgMembershipType membershipType,
            OrgMembershipSource source) {
        Optional<OrgMember> existing = orgMemberRepository.findByOrganizationIdAndUserId(organizationId, userId);
        if (existing.isEmpty()) {
            return orgMemberRepository.save(
                    OrgMember.create(organizationId, userId, membershipType, source));
        }
        OrgMember member = existing.get();
        if (member.status() == OrgMemberStatus.ACTIVE) {
            return member;
        }
        return orgMemberRepository.save(member.reinstate(membershipType, source));
    }
}

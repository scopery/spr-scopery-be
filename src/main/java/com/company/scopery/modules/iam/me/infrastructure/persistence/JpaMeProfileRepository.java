package com.company.scopery.modules.iam.me.infrastructure.persistence;

import com.company.scopery.modules.iam.me.domain.model.MeProfile;
import com.company.scopery.modules.iam.me.domain.model.MeProfileRepository;
import com.company.scopery.modules.iam.me.infrastructure.mapper.MeProfileMapper;
import com.company.scopery.modules.iam.user.domain.model.IamUserRepository;
import com.company.scopery.modules.workspace.orgmember.application.service.OrgMemberQueryService;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaMeProfileRepository implements MeProfileRepository {

    private final IamUserRepository userRepository;
    private final OrgMemberQueryService orgMemberQueryService;

    public JpaMeProfileRepository(IamUserRepository userRepository,
                                  OrgMemberQueryService orgMemberQueryService) {
        this.userRepository = userRepository;
        this.orgMemberQueryService = orgMemberQueryService;
    }

    @Override
    public Optional<MeProfile> findByUserId(UUID userId) {
        return userRepository.findById(userId).map(user ->
                MeProfileMapper.toProfile(user, orgMemberQueryService.findMembershipSummariesByUserId(userId)));
    }
}

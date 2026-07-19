package com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacitySortFields;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.resourcecapacity.shared.util.CapacityEnumParser;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.query.SearchUserCapacityProfileQuery;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.response.UserCapacityProfileResponse;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.enums.UserCapacityProfileStatus;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.model.UserCapacityProfile;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.model.UserCapacityProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserCapacityProfileQueryService {

    private final UserCapacityProfileRepository profileRepository;
    private final CapacityWorkspaceAuthorizationService authorizationService;

    public UserCapacityProfileQueryService(UserCapacityProfileRepository profileRepository,
                                           CapacityWorkspaceAuthorizationService authorizationService) {
        this.profileRepository = profileRepository;
        this.authorizationService = authorizationService;
    }

    @Transactional(readOnly = true)
    public UserCapacityProfileResponse getProfile(UUID id) {
        UserCapacityProfile profile = profileRepository.findById(id)
                .orElseThrow(() -> CapacityExceptions.profileNotFound(id));
        authorizationService.requireProfileView(profile.workspaceId());
        return UserCapacityProfileResponse.from(profile);
    }

    @Transactional(readOnly = true)
    public PageResult<UserCapacityProfileResponse> searchProfiles(SearchUserCapacityProfileQuery query) {
        authorizationService.requireProfileView(query.workspaceId());

        UserCapacityProfileStatus status = CapacityEnumParser.parseOptional(
                UserCapacityProfileStatus.class, query.status(),
                "CAPACITY_PROFILE_INVALID_STATUS", "status");

        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), CapacitySortFields.EFFECTIVE_FROM, false);

        return profileRepository
                .search(query.workspaceId(), query.workspaceMemberId(), query.userId(), status, pageQuery)
                .map(UserCapacityProfileResponse::from);
    }
}

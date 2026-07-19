package com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.action;

import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityActivityActions;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityEntityTypes;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.resourcecapacity.shared.support.CapacityPlatformPublisher;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.command.ActivateUserCapacityProfileCommand;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.response.UserCapacityProfileResponse;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.model.UserCapacityProfile;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.model.UserCapacityProfileRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ActivateUserCapacityProfileAction {

    private final UserCapacityProfileRepository profileRepository;
    private final CapacityActivityLogger activityLogger;
    private final CapacityWorkspaceAuthorizationService authorizationService;
    private final CapacityPlatformPublisher platformPublisher;

    public ActivateUserCapacityProfileAction(UserCapacityProfileRepository profileRepository,
                                             CapacityActivityLogger activityLogger,
                                             CapacityWorkspaceAuthorizationService authorizationService,
                                             CapacityPlatformPublisher platformPublisher) {
        this.profileRepository = profileRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public UserCapacityProfileResponse execute(ActivateUserCapacityProfileCommand cmd) {
        UserCapacityProfile profile = profileRepository.findById(cmd.id())
                .orElseThrow(() -> CapacityExceptions.profileNotFound(cmd.id()));
        authorizationService.requireProfileUpdate(profile.workspaceId());

        for (UserCapacityProfile existing : profileRepository.findActiveByWorkspaceMemberId(profile.workspaceMemberId())) {
            if (!existing.id().equals(profile.id()) && existing.overlaps(profile.effectiveFrom(), profile.effectiveTo())) {
                throw CapacityExceptions.profileOverlap(profile.workspaceMemberId());
            }
        }

        UserCapacityProfile saved = profileRepository.save(profile.activate());

        platformPublisher.enqueueProfile(saved, "CAPACITY_PROFILE_ACTIVATED");
        activityLogger.logSuccess(
                CapacityEntityTypes.USER_CAPACITY_PROFILE,
                saved.id(),
                CapacityActivityActions.CAPACITY_PROFILE_ACTIVATED,
                "Capacity profile activated: " + saved.id());

        return UserCapacityProfileResponse.from(saved);
    }
}

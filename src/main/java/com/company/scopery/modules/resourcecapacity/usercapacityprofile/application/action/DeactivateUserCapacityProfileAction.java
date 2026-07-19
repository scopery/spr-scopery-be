package com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.action;

import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityActivityActions;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityEntityTypes;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.resourcecapacity.shared.support.CapacityPlatformPublisher;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.command.DeactivateUserCapacityProfileCommand;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.response.UserCapacityProfileResponse;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.model.UserCapacityProfile;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.model.UserCapacityProfileRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeactivateUserCapacityProfileAction {

    private final UserCapacityProfileRepository profileRepository;
    private final CapacityActivityLogger activityLogger;
    private final CapacityWorkspaceAuthorizationService authorizationService;
    private final CapacityPlatformPublisher platformPublisher;

    public DeactivateUserCapacityProfileAction(UserCapacityProfileRepository profileRepository,
                                               CapacityActivityLogger activityLogger,
                                               CapacityWorkspaceAuthorizationService authorizationService,
                                               CapacityPlatformPublisher platformPublisher) {
        this.profileRepository = profileRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public UserCapacityProfileResponse execute(DeactivateUserCapacityProfileCommand cmd) {
        UserCapacityProfile profile = profileRepository.findById(cmd.id())
                .orElseThrow(() -> CapacityExceptions.profileNotFound(cmd.id()));
        authorizationService.requireProfileUpdate(profile.workspaceId());

        UserCapacityProfile saved = profileRepository.save(profile.deactivate());

        platformPublisher.enqueueProfile(saved, "CAPACITY_PROFILE_DEACTIVATED");
        activityLogger.logSuccess(
                CapacityEntityTypes.USER_CAPACITY_PROFILE,
                saved.id(),
                CapacityActivityActions.CAPACITY_PROFILE_DEACTIVATED,
                "Capacity profile deactivated: " + saved.id());

        return UserCapacityProfileResponse.from(saved);
    }
}

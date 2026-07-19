package com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.action;

import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityActivityActions;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityEntityTypes;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.resourcecapacity.shared.support.CapacityPlatformPublisher;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.command.ArchiveUserCapacityProfileCommand;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.response.UserCapacityProfileResponse;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.model.UserCapacityProfile;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.model.UserCapacityProfileRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ArchiveUserCapacityProfileAction {

    private final UserCapacityProfileRepository profileRepository;
    private final CapacityActivityLogger activityLogger;
    private final CapacityWorkspaceAuthorizationService authorizationService;
    private final CapacityPlatformPublisher platformPublisher;

    public ArchiveUserCapacityProfileAction(UserCapacityProfileRepository profileRepository,
                                            CapacityActivityLogger activityLogger,
                                            CapacityWorkspaceAuthorizationService authorizationService,
                                            CapacityPlatformPublisher platformPublisher) {
        this.profileRepository = profileRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public UserCapacityProfileResponse execute(ArchiveUserCapacityProfileCommand cmd) {
        UserCapacityProfile profile = profileRepository.findById(cmd.id())
                .orElseThrow(() -> CapacityExceptions.profileNotFound(cmd.id()));
        authorizationService.requireProfileArchive(profile.workspaceId());

        UserCapacityProfile saved = profileRepository.save(profile.archive(null));

        platformPublisher.enqueueProfile(saved, "CAPACITY_PROFILE_ARCHIVED");
        activityLogger.logSuccess(
                CapacityEntityTypes.USER_CAPACITY_PROFILE,
                saved.id(),
                CapacityActivityActions.CAPACITY_PROFILE_ARCHIVED,
                "Capacity profile archived: " + saved.id());

        return UserCapacityProfileResponse.from(saved);
    }
}

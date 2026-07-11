package com.company.scopery.modules.iam.me.application.service;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.me.application.query.GetMeQuery;
import com.company.scopery.modules.iam.me.application.response.MeResponse;
import com.company.scopery.modules.iam.me.domain.model.MeProfileRepository;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MeQueryService {

    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final MeProfileRepository meProfileRepository;

    public MeQueryService(CurrentUserAuthorizationService currentUserAuthorizationService,
                          MeProfileRepository meProfileRepository) {
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.meProfileRepository = meProfileRepository;
    }

    @Transactional(readOnly = true)
    public MeResponse getMe(GetMeQuery query) {
        var userId = currentUserAuthorizationService.resolveCurrentUser().id();
        var profile = meProfileRepository.findByUserId(userId)
                .orElseThrow(() -> IamExceptions.iamUserNotFound(userId));
        return MeResponse.from(profile);
    }
}

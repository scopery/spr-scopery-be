package com.company.scopery.modules.iam.authorization.application;

import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.user.domain.IamUser;
import com.company.scopery.modules.iam.user.domain.IamUserRepository;
import com.company.scopery.modules.iam.user.domain.Username;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserAuthorizationService {

    private final IamUserRepository userRepository;

    public CurrentUserAuthorizationService(IamUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public IamUser resolveCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw IamExceptions.authenticatedUserCannotBeResolved();
        }
        String username = auth.getName();
        return userRepository.findByUsername(new Username(username))
                .orElseThrow(() -> IamExceptions.authenticatedUserNotFound(username));
    }
}

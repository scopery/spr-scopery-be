package com.company.scopery.bootstrap.seed;

import com.company.scopery.modules.iam.role.domain.IamRoleCode;
import com.company.scopery.modules.iam.role.domain.IamRoleRepository;
import com.company.scopery.modules.iam.roleassignment.domain.IamRoleAssignment;
import com.company.scopery.modules.iam.roleassignment.domain.IamRoleAssignmentRepository;
import com.company.scopery.modules.iam.roleassignment.domain.RoleAssigneeType;
import com.company.scopery.modules.iam.user.domain.EmailAddress;
import com.company.scopery.modules.iam.user.domain.IamUser;
import com.company.scopery.modules.iam.user.domain.IamUserRepository;
import com.company.scopery.modules.iam.user.domain.Username;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(20)
public class AdminUserSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(AdminUserSeedInitializer.class);

    @Value("${admin.seed.username:}")
    private String adminUsername;

    @Value("${admin.seed.email:}")
    private String adminEmail;

    @Value("${admin.seed.password:}")
    private String adminPassword;

    private final IamUserRepository userRepository;
    private final IamRoleRepository roleRepository;
    private final IamRoleAssignmentRepository roleAssignmentRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserSeedInitializer(IamUserRepository userRepository,
                                     IamRoleRepository roleRepository,
                                     IamRoleAssignmentRepository roleAssignmentRepository,
                                     PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.roleAssignmentRepository = roleAssignmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (adminUsername == null || adminUsername.isBlank() ||
            adminEmail == null || adminEmail.isBlank() ||
            adminPassword == null || adminPassword.isBlank()) {
            log.info("[AdminSeed] ADMIN_USERNAME/EMAIL/PASSWORD not set — skipping admin seed");
            return;
        }

        Username username = Username.of(adminUsername);
        if (userRepository.existsByUsername(username)) {
            log.info("[AdminSeed] Admin user already exists: {}", adminUsername);
            return;
        }

        IamUser admin = IamUser.create(username, EmailAddress.of(adminEmail), "Admin")
                .withPassword(passwordEncoder.encode(adminPassword));
        IamUser saved = userRepository.save(admin);

        var superAdminRole = roleRepository.findByCode(IamRoleCode.of("SUPER_ADMIN"));
        if (superAdminRole.isPresent()) {
            roleAssignmentRepository.save(IamRoleAssignment.create(
                    RoleAssigneeType.USER, saved.id(), superAdminRole.get().id(), null, saved.id()));
            log.info("[AdminSeed] Admin user seeded: {}", adminUsername);
        } else {
            log.warn("[AdminSeed] SUPER_ADMIN role not found — admin user created without role assignment");
        }
    }
}

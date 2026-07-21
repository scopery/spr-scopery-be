package com.company.scopery.bootstrap.seed;

import com.company.scopery.modules.iam.role.domain.valueobject.IamRoleCode;
import com.company.scopery.modules.iam.role.domain.model.IamRoleRepository;
import com.company.scopery.modules.iam.roleassignment.domain.model.IamRoleAssignment;
import com.company.scopery.modules.iam.roleassignment.domain.model.IamRoleAssignmentRepository;
import com.company.scopery.modules.iam.roleassignment.domain.enums.RoleAssigneeType;
import com.company.scopery.modules.iam.user.domain.enums.IamRegistrationSource;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.model.IamUserRepository;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;
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
        var superAdminRole = roleRepository.findByCode(IamRoleCode.of("SUPER_ADMIN"));
        if (superAdminRole.isEmpty()) {
            log.warn("[AdminSeed] SUPER_ADMIN role not found — skipping admin seed");
            return;
        }

        IamUser saved;
        if (userRepository.existsByUsername(username)) {
            saved = userRepository.findByUsername(username).orElse(null);
            if (saved == null) {
                log.warn("[AdminSeed] Admin user lookup failed after existsByUsername check");
                return;
            }
            log.info("[AdminSeed] Admin user already exists: {}", adminUsername);
        } else {
            IamUser admin = IamUser.create(username, EmailAddress.of(adminEmail), "Admin",
                            IamRegistrationSource.SYSTEM_BOOTSTRAP)
                    .withPassword(passwordEncoder.encode(adminPassword));
            saved = userRepository.save(admin);
            log.info("[AdminSeed] Admin user created: {}", adminUsername);
        }

        if (!roleAssignmentRepository.existsActiveAssignment(
                RoleAssigneeType.USER, saved.id(), superAdminRole.get().id(), null)) {
            roleAssignmentRepository.save(IamRoleAssignment.create(
                    RoleAssigneeType.USER, saved.id(), superAdminRole.get().id(), null, saved.id()));
            log.info("[AdminSeed] SUPER_ADMIN role assigned to admin user: {}", adminUsername);
        }
    }
}

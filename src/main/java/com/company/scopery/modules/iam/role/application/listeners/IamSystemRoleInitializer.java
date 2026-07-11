package com.company.scopery.modules.iam.role.application.listeners;

import com.company.scopery.modules.iam.role.domain.model.IamRole;
import com.company.scopery.modules.iam.role.domain.valueobject.IamRoleCode;
import com.company.scopery.modules.iam.role.domain.model.IamRoleRepository;
import com.company.scopery.modules.iam.role.domain.enums.IamRoleSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Seeds built-in system roles on startup.
 * Must run after IamRightCatalogInitializer (order 2 vs default 0 for right catalog).
 */
@Component
@Order(2)
public class IamSystemRoleInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(IamSystemRoleInitializer.class);

    private final IamRoleRepository roleRepository;

    public IamSystemRoleInitializer(IamRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        List<RoleDef> catalog = buildCatalog();
        int seeded = 0;
        for (RoleDef def : catalog) {
            IamRoleCode code = IamRoleCode.of(def.code);
            if (!roleRepository.existsByCode(code)) {
                IamRole role = IamRole.createSystem(code, def.name, def.description, def.source, null);
                roleRepository.save(role);
                seeded++;
            }
        }
        if (seeded > 0) {
            log.info("IAM system role catalog: seeded {} roles", seeded);
        }
    }

    private List<RoleDef> buildCatalog() {
        return List.of(
                new RoleDef(
                        "SUPER_ADMIN",
                        "Super Admin",
                        "System-wide super administrator with unrestricted access",
                        IamRoleSource.SYSTEM_BUILT_IN),
                new RoleDef(
                        "SYSTEM_GOVERNANCE_ADMIN",
                        "System Governance Admin",
                        "Template role for system governance — manages system roles and document type configuration",
                        IamRoleSource.SYSTEM_TEMPLATE)
        );
    }

    private record RoleDef(String code, String name, String description, IamRoleSource source) {}
}

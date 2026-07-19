package com.company.scopery.modules.ratecard.shared.listeners;

import com.company.scopery.modules.ratecard.costrole.domain.enums.CostRoleScope;
import com.company.scopery.modules.ratecard.costrole.domain.model.CostRole;
import com.company.scopery.modules.ratecard.costrole.domain.model.CostRoleRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Order(20)
public class CostRoleSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final CostRoleRepository costRoleRepository;

    public CostRoleSeedInitializer(CostRoleRepository costRoleRepository) {
        this.costRoleRepository = costRoleRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        for (SeedRole seed : SEEDS) {
            if (costRoleRepository.existsByScopeAndCode(CostRoleScope.SYSTEM, null, null, seed.code())) {
                continue;
            }
            CostRole role = CostRole.createSystemBuiltIn(seed.code(), seed.name(), seed.category());
            costRoleRepository.save(role);
        }
    }

    private record SeedRole(String code, String name, String category) {}

    static final List<SeedRole> SEEDS = List.of(
            new SeedRole("PROJECT_MANAGER", "Project Manager", "MANAGEMENT"),
            new SeedRole("BUSINESS_ANALYST", "Business Analyst", "ANALYSIS"),
            new SeedRole("SOLUTION_ARCHITECT", "Solution Architect", "ARCHITECTURE"),
            new SeedRole("TECH_LEAD", "Tech Lead", "ENGINEERING"),
            new SeedRole("FRONTEND_DEVELOPER", "Frontend Developer", "ENGINEERING"),
            new SeedRole("BACKEND_DEVELOPER", "Backend Developer", "ENGINEERING"),
            new SeedRole("FULLSTACK_DEVELOPER", "Fullstack Developer", "ENGINEERING"),
            new SeedRole("MOBILE_DEVELOPER", "Mobile Developer", "ENGINEERING"),
            new SeedRole("QA_ENGINEER", "QA Engineer", "QUALITY"),
            new SeedRole("DEVOPS_ENGINEER", "DevOps Engineer", "ENGINEERING"),
            new SeedRole("UX_UI_DESIGNER", "UX/UI Designer", "DESIGN"),
            new SeedRole("DATA_ENGINEER", "Data Engineer", "DATA"),
            new SeedRole("AI_ENGINEER", "AI Engineer", "DATA")
    );

    public static List<SeedRole> seeds() {
        return SEEDS;
    }
}

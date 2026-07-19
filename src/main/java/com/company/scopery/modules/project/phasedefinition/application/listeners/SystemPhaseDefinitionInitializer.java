package com.company.scopery.modules.project.phasedefinition.application.listeners;

import com.company.scopery.modules.project.phasedefinition.domain.enums.PhaseDefinitionScope;
import com.company.scopery.modules.project.phasedefinition.domain.model.PhaseDefinition;
import com.company.scopery.modules.project.phasedefinition.domain.model.PhaseDefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Component
@Order(300)
public class SystemPhaseDefinitionInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(SystemPhaseDefinitionInitializer.class);

    private final PhaseDefinitionRepository repository;

    public SystemPhaseDefinitionInitializer(PhaseDefinitionRepository repository) {
        this.repository = repository;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        seedIfAbsent("DISCOVERY",   "Discovery",   "Initial discovery and feasibility study", 1, true);
        seedIfAbsent("ANALYSIS",    "Analysis",    "Requirements analysis and scoping",        2, true);
        seedIfAbsent("DESIGN",      "Design",      "Architecture and design phase",            3, true);
        seedIfAbsent("DEVELOPMENT", "Development", "Implementation and development phase",     4, true);
        seedIfAbsent("TESTING",     "Testing",     "Quality assurance and testing phase",      5, true);
        seedIfAbsent("DEPLOYMENT",  "Deployment",  "Deployment and release phase",             6, true);
        seedIfAbsent("SUPPORT",     "Support",     "Post-release support and operations",      7, true);
        seedIfAbsent("CLOSURE",     "Closure",     "Project closure and handover",             8, true);
        // Kept for backward compatibility with existing workspaces/projects
        seedIfAbsent("MAINTENANCE", "Maintenance", "Post-release maintenance and support",     9, true);
    }

    private void seedIfAbsent(String code, String name, String description,
                               int displayOrder, boolean isSystemDefault) {
        if (repository.existsByCodeAndScope(code, PhaseDefinitionScope.SYSTEM)) {
            return;
        }
        try {
            PhaseDefinition pd = PhaseDefinition.createSystem(code, name, description, displayOrder, isSystemDefault);
            repository.save(pd);
            log.info("Seeded system phase definition: {}", code);
        } catch (DataIntegrityViolationException | ObjectOptimisticLockingFailureException ex) {
            log.info("System phase definition {} already present", code);
        }
    }
}

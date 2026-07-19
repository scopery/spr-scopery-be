package com.company.scopery.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;

class ArchitectureRulesTest {

    private static final JavaClasses CLASSES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.company.scopery");

    @Test
    void common_mustNotDependOnModules() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.company.scopery.common..")
                .should().dependOnClassesThat().resideInAPackage("com.company.scopery.modules..");
        rule.check(CLASSES);
    }

    @Test
    void eventregistry_domain_mustNotImportJpaOrSpringWeb() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.company.scopery.modules.eventregistry..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "jakarta.persistence..",
                        "org.springframework.web..");
        rule.check(CLASSES);
    }

    @Test
    void workspace_domain_mustNotImportJpaOrSpringWeb() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.company.scopery.modules.workspace..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "jakarta.persistence..",
                        "org.springframework.web..");
        rule.check(CLASSES);
    }
}

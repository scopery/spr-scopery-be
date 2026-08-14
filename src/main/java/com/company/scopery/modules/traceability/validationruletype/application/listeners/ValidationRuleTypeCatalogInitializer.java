package com.company.scopery.modules.traceability.validationruletype.application.listeners;

import com.company.scopery.modules.traceability.validationruletype.domain.model.RegistryValidationRuleType;
import com.company.scopery.modules.traceability.validationruletype.domain.model.RegistryValidationRuleTypeRepository;
import com.company.scopery.modules.traceability.validationruletype.infrastructure.persistence.SpringDataRegistryValidationRuleTypeJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Component
public class ValidationRuleTypeCatalogInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(ValidationRuleTypeCatalogInitializer.class);

    private final RegistryValidationRuleTypeRepository repo;
    private final SpringDataRegistryValidationRuleTypeJpaRepository springData;

    public ValidationRuleTypeCatalogInitializer(RegistryValidationRuleTypeRepository repo,
                                                  SpringDataRegistryValidationRuleTypeJpaRepository springData) {
        this.repo = repo;
        this.springData = springData;
    }

    private record Seed(String code, String name, String category, String paramSchemaJson, String defaultMessage) {}

    private static final List<Seed> SYSTEM_SEEDS = List.of(
            new Seed("REGEX", "Regular Expression", "FORMAT", "{\"pattern\":\"string\"}", "Invalid format"),
            new Seed("MAX_LENGTH", "Maximum Length", "RANGE", "{\"maxLength\":\"integer\"}", "Value exceeds maximum length"),
            new Seed("IN_LIST", "Allowed Values", "REFERENCE", "{\"values\":[\"string\"]}", "Value is not in the allowed list"),
            new Seed("FILE_SIZE", "File Size Limit", "RANGE", "{\"maxBytes\":\"integer\"}", "File size exceeds limit"),
            new Seed("FILE_TYPE", "File Type", "FORMAT", "{\"mimeTypes\":[\"string\"]}", "File type is not allowed"),
            new Seed("DATE_FORMAT", "Date Format", "FORMAT", "{\"format\":\"string\"}", "Invalid date format"),
            new Seed("URL", "URL Format", "FORMAT", null, "Invalid URL format"),
            new Seed("HALF_WIDTH", "Half-width Characters", "FORMAT", null, "Must be half-width characters"),
            new Seed("EMAIL_FORMAT", "Email Format", "FORMAT", null, "Invalid email format"),
            new Seed("POSTAL_CODE_JP", "Japanese Postal Code", "FORMAT", null, "Invalid postal code format"),
            new Seed("PHONE_NUMBER_JP", "Japanese Phone Number", "FORMAT", null, "Invalid phone number format"),
            new Seed("MATCHING", "Field Matching", "REFERENCE", "{\"targetFieldKey\":\"string\"}", "Fields do not match"),
            new Seed("REQUIRED", "Conditional Required", "CONDITIONAL", "{\"condition\":{\"fieldKey\":\"string\",\"op\":\"string\"}}", "This field is required"),
            new Seed("UNIQUE", "Unique Value", "REFERENCE", null, "Value must be unique")
    );

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        int seeded = 0;
        for (int i = 0; i < SYSTEM_SEEDS.size(); i++) {
            Seed seed = SYSTEM_SEEDS.get(i);
            try {
                if (!springData.existsByCodeAndWorkspaceIdIsNull(seed.code())) {
                    UUID deterministicId = UUID.nameUUIDFromBytes(("vrt:" + seed.code()).getBytes(StandardCharsets.UTF_8));
                    RegistryValidationRuleType domain = RegistryValidationRuleType.createSystem(
                            deterministicId, seed.code(), seed.name(), seed.category(),
                            seed.paramSchemaJson(), seed.defaultMessage(), i);
                    repo.save(domain);
                    seeded++;
                }
            } catch (Exception ex) {
                log.warn("Failed to seed validation rule type [{}]: {}", seed.code(), ex.getMessage());
            }
        }
        if (seeded > 0) {
            log.info("Seeded {} system validation rule type(s)", seeded);
        }
    }
}

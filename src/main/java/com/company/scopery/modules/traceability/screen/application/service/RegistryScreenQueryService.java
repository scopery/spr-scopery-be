package com.company.scopery.modules.traceability.screen.application.service;

import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponent;
import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponentRepository;
import com.company.scopery.modules.traceability.componentoption.domain.model.RegistryComponentOption;
import com.company.scopery.modules.traceability.componentoption.domain.model.RegistryComponentOptionRepository;
import com.company.scopery.modules.traceability.dataentityfield.domain.model.RegistryDataEntityField;
import com.company.scopery.modules.traceability.dataentityfield.domain.model.RegistryDataEntityFieldRepository;
import com.company.scopery.modules.traceability.fieldmodeconfig.domain.model.RegistryScreenFieldModeConfig;
import com.company.scopery.modules.traceability.fieldmodeconfig.domain.model.RegistryScreenFieldModeConfigRepository;
import com.company.scopery.modules.traceability.fieldvalidation.domain.model.RegistryScreenFieldValidation;
import com.company.scopery.modules.traceability.fieldvalidation.domain.model.RegistryScreenFieldValidationRepository;
import com.company.scopery.modules.traceability.screen.application.response.RegistryScreenResponse;
import com.company.scopery.modules.traceability.screen.application.response.ScreenFullSpecResponse;
import com.company.scopery.modules.traceability.screen.application.response.ScreenFullSpecResponse.*;
import com.company.scopery.modules.traceability.screen.domain.model.RegistryScreen;
import com.company.scopery.modules.traceability.screen.domain.model.RegistryScreenRepository;
import com.company.scopery.modules.traceability.screenaction.domain.model.RegistryScreenAction;
import com.company.scopery.modules.traceability.screenaction.domain.model.RegistryScreenActionRepository;
import com.company.scopery.modules.traceability.screeneventitem.domain.model.RegistryScreenEventItem;
import com.company.scopery.modules.traceability.screeneventitem.domain.model.RegistryScreenEventItemRepository;
import com.company.scopery.modules.traceability.screenfield.domain.model.RegistryScreenField;
import com.company.scopery.modules.traceability.screenfield.domain.model.RegistryScreenFieldRepository;
import com.company.scopery.modules.traceability.screenmode.domain.model.RegistryScreenMode;
import com.company.scopery.modules.traceability.screenmode.domain.model.RegistryScreenModeRepository;
import com.company.scopery.modules.traceability.screenprocessitem.domain.model.RegistryScreenProcessItem;
import com.company.scopery.modules.traceability.screenprocessitem.domain.model.RegistryScreenProcessItemRepository;
import com.company.scopery.modules.traceability.screensection.domain.model.RegistryScreenSection;
import com.company.scopery.modules.traceability.screensection.domain.model.RegistryScreenSectionRepository;
import com.company.scopery.modules.knowledge.shared.storage.ObjectStorageProvider;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.validationruletype.domain.model.RegistryValidationRuleType;
import com.company.scopery.modules.traceability.validationruletype.domain.model.RegistryValidationRuleTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RegistryScreenQueryService {

    private final RegistryScreenRepository repo;
    private final RegistryScreenModeRepository modeRepo;
    private final RegistryScreenSectionRepository sectionRepo;
    private final RegistryScreenActionRepository screenActionRepo;
    private final RegistryScreenFieldRepository fieldRepo;
    private final RegistryScreenFieldModeConfigRepository modeConfigRepo;
    private final RegistryScreenFieldValidationRepository validationRepo;
    private final RegistryAppComponentRepository componentRepo;
    private final RegistryComponentOptionRepository optionRepo;
    private final RegistryDataEntityFieldRepository dataFieldRepo;
    private final RegistryValidationRuleTypeRepository ruleTypeRepo;
    private final RegistryScreenProcessItemRepository processItemRepo;
    private final RegistryScreenEventItemRepository eventItemRepo;
    private final TraceabilityAuthorizationService authorization;
    private final ObjectStorageProvider storageProvider;

    public RegistryScreenQueryService(
            RegistryScreenRepository repo,
            RegistryScreenModeRepository modeRepo,
            RegistryScreenSectionRepository sectionRepo,
            RegistryScreenActionRepository screenActionRepo,
            RegistryScreenFieldRepository fieldRepo,
            RegistryScreenFieldModeConfigRepository modeConfigRepo,
            RegistryScreenFieldValidationRepository validationRepo,
            RegistryAppComponentRepository componentRepo,
            RegistryComponentOptionRepository optionRepo,
            RegistryDataEntityFieldRepository dataFieldRepo,
            RegistryValidationRuleTypeRepository ruleTypeRepo,
            RegistryScreenProcessItemRepository processItemRepo,
            RegistryScreenEventItemRepository eventItemRepo,
            TraceabilityAuthorizationService authorization,
            ObjectStorageProvider storageProvider) {
        this.repo = repo;
        this.modeRepo = modeRepo;
        this.sectionRepo = sectionRepo;
        this.screenActionRepo = screenActionRepo;
        this.fieldRepo = fieldRepo;
        this.modeConfigRepo = modeConfigRepo;
        this.validationRepo = validationRepo;
        this.componentRepo = componentRepo;
        this.optionRepo = optionRepo;
        this.dataFieldRepo = dataFieldRepo;
        this.ruleTypeRepo = ruleTypeRepo;
        this.processItemRepo = processItemRepo;
        this.eventItemRepo = eventItemRepo;
        this.authorization = authorization;
        this.storageProvider = storageProvider;
    }

    @Transactional(readOnly = true)
    public List<RegistryScreenResponse> list(UUID workspaceId, UUID applicationId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByApplicationId(applicationId).stream()
                .map(s -> RegistryScreenResponse.from(s, mockupUrlFor(s.mockupObjectKey())))
                .toList();
    }

    @Transactional(readOnly = true)
    public RegistryScreenResponse get(UUID workspaceId, UUID applicationId, UUID id) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByIdAndApplicationId(id, applicationId)
                .map(s -> RegistryScreenResponse.from(s, mockupUrlFor(s.mockupObjectKey())))
                .orElseThrow(() -> TraceabilityExceptions.applicationNotFound(id));
    }

    private String mockupUrlFor(String objectKey) {
        if (objectKey == null) return null;
        return storageProvider.createPresignedDownload(objectKey, null).downloadUrl();
    }

    @Transactional(readOnly = true)
    public ScreenFullSpecResponse getFullSpec(UUID workspaceId, UUID screenId) {
        authorization.requireWorkspaceView(workspaceId);

        // Q1: screen
        RegistryScreen screen = repo.findById(screenId)
                .orElseThrow(() -> TraceabilityExceptions.screenNotFound(screenId));

        // Q2: modes (ACTIVE only)
        List<RegistryScreenMode> allModes = modeRepo.findByScreenId(screenId);
        List<RegistryScreenMode> activeModes = allModes.stream()
                .filter(m -> "ACTIVE".equals(m.status().name()))
                .sorted(Comparator.comparingInt(RegistryScreenMode::displayOrder))
                .toList();
        Map<UUID, RegistryScreenMode> modesById = allModes.stream()
                .collect(Collectors.toMap(RegistryScreenMode::id, Function.identity()));

        // Q3: sections (ACTIVE only)
        List<RegistryScreenSection> sections = sectionRepo.findByScreenId(screenId).stream()
                .filter(s -> "ACTIVE".equals(s.status().name()))
                .sorted(Comparator.comparingInt(RegistryScreenSection::displayOrder))
                .toList();

        // Q4: screen actions (ACTIVE only)
        List<RegistryScreenAction> screenActions = screenActionRepo.findByScreenId(screenId).stream()
                .filter(a -> "ACTIVE".equals(a.status().name()))
                .sorted(Comparator.comparingInt(RegistryScreenAction::displayOrder))
                .toList();

        // Q5: fields (ACTIVE only)
        List<RegistryScreenField> fields = fieldRepo.findByScreenId(screenId).stream()
                .filter(f -> "ACTIVE".equals(f.status().name()))
                .sorted(Comparator.comparingInt(RegistryScreenField::displayOrder))
                .toList();

        List<UUID> fieldIds = fields.stream().map(RegistryScreenField::id).toList();

        // Q6: mode configs (empty list when no fields)
        List<RegistryScreenFieldModeConfig> modeConfigs = fieldIds.isEmpty() ? List.of() : modeConfigRepo.findByFieldIdIn(fieldIds);
        Map<UUID, List<RegistryScreenFieldModeConfig>> configsByFieldId = modeConfigs.stream()
                .collect(Collectors.groupingBy(RegistryScreenFieldModeConfig::fieldId));

        // Q7: validations (empty list when no fields)
        List<RegistryScreenFieldValidation> validations = fieldIds.isEmpty() ? List.of() : validationRepo.findByFieldIdIn(fieldIds);
        Map<UUID, List<RegistryScreenFieldValidation>> validationsByFieldId = validations.stream()
                .filter(v -> "ACTIVE".equals(v.status().name()))
                .collect(Collectors.groupingBy(RegistryScreenFieldValidation::fieldId));

        // Q8: components
        Set<UUID> componentIds = fields.stream()
                .map(RegistryScreenField::componentId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<UUID, RegistryAppComponent> componentsById = componentIds.isEmpty() ? Map.of() :
                componentRepo.findByIdIn(componentIds).stream()
                        .collect(Collectors.toMap(RegistryAppComponent::id, Function.identity()));

        // Q9: options for STATIC components only
        Set<UUID> staticComponentIds = componentsById.values().stream()
                .filter(c -> "STATIC".equals(c.optionSourceType()))
                .map(RegistryAppComponent::id)
                .collect(Collectors.toSet());
        Map<UUID, List<RegistryComponentOption>> optionsByComponentId = staticComponentIds.isEmpty() ? Map.of() :
                optionRepo.findByComponentIdIn(staticComponentIds).stream()
                        .filter(o -> "ACTIVE".equals(o.status().name()))
                        .collect(Collectors.groupingBy(RegistryComponentOption::componentId));

        // Q10: data entity fields
        Set<UUID> dataFieldIds = fields.stream()
                .map(RegistryScreenField::dataEntityFieldId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<UUID, RegistryDataEntityField> dataFieldsById = dataFieldIds.isEmpty() ? Map.of() :
                dataFieldRepo.findByIdIn(dataFieldIds).stream()
                        .collect(Collectors.toMap(RegistryDataEntityField::id, Function.identity()));

        // Q11: validation rule types
        Set<UUID> ruleTypeIds = validations.stream()
                .map(RegistryScreenFieldValidation::ruleTypeId)
                .collect(Collectors.toSet());
        Map<UUID, RegistryValidationRuleType> ruleTypesById = ruleTypeIds.isEmpty() ? Map.of() :
                ruleTypeRepo.findByIdIn(ruleTypeIds).stream()
                        .collect(Collectors.toMap(RegistryValidationRuleType::id, Function.identity()));

        // Q12: process items
        List<RegistryScreenProcessItem> processItems = processItemRepo
                .findByScreenIdAndStatusOrderByDisplayOrderAsc(screenId, "ACTIVE");

        // Q13: event items
        List<RegistryScreenEventItem> eventItems = eventItemRepo
                .findByScreenIdAndStatusOrderByDisplayOrderAsc(screenId, "ACTIVE");

        return buildResponse(screen, activeModes, sections, screenActions, fields, processItems, eventItems, modesById,
                configsByFieldId, validationsByFieldId, componentsById, optionsByComponentId,
                dataFieldsById, ruleTypesById);
    }

    private ScreenFullSpecResponse buildResponse(
            RegistryScreen screen,
            List<RegistryScreenMode> activeModes,
            List<RegistryScreenSection> sections,
            List<RegistryScreenAction> screenActions,
            List<RegistryScreenField> fields,
            List<RegistryScreenProcessItem> processItems,
            List<RegistryScreenEventItem> eventItems,
            Map<UUID, RegistryScreenMode> modesById,
            Map<UUID, List<RegistryScreenFieldModeConfig>> configsByFieldId,
            Map<UUID, List<RegistryScreenFieldValidation>> validationsByFieldId,
            Map<UUID, RegistryAppComponent> componentsById,
            Map<UUID, List<RegistryComponentOption>> optionsByComponentId,
            Map<UUID, RegistryDataEntityField> dataFieldsById,
            Map<UUID, RegistryValidationRuleType> ruleTypesById) {

        List<ModeEntry> modeEntries = activeModes.stream()
                .map(m -> new ModeEntry(m.id(), m.modeCode(), m.name(), m.displayOrder(), m.status().name()))
                .toList();

        List<SectionEntry> sectionEntries = sections.stream()
                .map(s -> new SectionEntry(s.id(), s.name(), s.description(), s.displayOrder(), s.status().name()))
                .toList();

        List<ScreenActionEntry> actionEntries = screenActions.stream()
                .map(a -> new ScreenActionEntry(a.id(), a.actionCode(), a.name(), a.actionType(), a.description(), a.displayOrder(), a.status().name()))
                .toList();

        List<FieldExportEntry> fieldEntries = fields.stream()
                .map(f -> buildFieldEntry(f, modesById, configsByFieldId, validationsByFieldId,
                        componentsById, optionsByComponentId, dataFieldsById, ruleTypesById))
                .toList();

        List<ProcessItemEntry> processEntries = processItems.stream()
                .map(p -> {
                    String modeCode = p.modeId() != null && modesById.containsKey(p.modeId())
                            ? modesById.get(p.modeId()).modeCode() : null;
                    return new ProcessItemEntry(p.id(), p.modeId(), modeCode, p.targetFieldId(),
                            p.title(), p.content(), p.sourceTable(), p.conditionNote(), p.displayOrder());
                })
                .toList();

        List<EventItemEntry> eventEntries = eventItems.stream()
                .map(e -> {
                    String modeCode = e.modeId() != null && modesById.containsKey(e.modeId())
                            ? modesById.get(e.modeId()).modeCode() : null;
                    return new EventItemEntry(e.id(), e.modeId(), modeCode, e.triggerFieldId(),
                            e.triggerActionCode(), e.title(), e.content(), e.conditionNote(),
                            e.targetScreenId(), e.targetModeCode(), e.displayOrder());
                })
                .toList();

        return new ScreenFullSpecResponse(screen.id(), screen.code(), screen.name(), screen.routePath(),
                screen.status().name(), modeEntries, sectionEntries, fieldEntries, actionEntries,
                processEntries, eventEntries);
    }

    private FieldExportEntry buildFieldEntry(
            RegistryScreenField field,
            Map<UUID, RegistryScreenMode> modesById,
            Map<UUID, List<RegistryScreenFieldModeConfig>> configsByFieldId,
            Map<UUID, List<RegistryScreenFieldValidation>> validationsByFieldId,
            Map<UUID, RegistryAppComponent> componentsById,
            Map<UUID, List<RegistryComponentOption>> optionsByComponentId,
            Map<UUID, RegistryDataEntityField> dataFieldsById,
            Map<UUID, RegistryValidationRuleType> ruleTypesById) {

        // Component with options
        ScreenFullSpecResponse.ComponentSummaryEntry componentEntry = null;
        if (field.componentId() != null && componentsById.containsKey(field.componentId())) {
            RegistryAppComponent comp = componentsById.get(field.componentId());
            List<ScreenFullSpecResponse.OptionEntry> optionEntries = "STATIC".equals(comp.optionSourceType())
                    ? optionsByComponentId.getOrDefault(comp.id(), List.of()).stream()
                        .sorted(Comparator.comparingInt(RegistryComponentOption::displayOrder))
                        .map(o -> new ScreenFullSpecResponse.OptionEntry(o.id(), o.optionValue(), o.optionLabel(), o.displayOrder()))
                        .toList()
                    : null;
            componentEntry = new ScreenFullSpecResponse.ComponentSummaryEntry(comp.id(), comp.code(), comp.name(),
                    comp.componentType(), comp.optionSourceType(),
                    comp.sourceEntityId(), comp.sourceValueColumn(), comp.sourceLabelColumn(), comp.sourceFilterJson(),
                    optionEntries);
        }

        // Data entity field summary
        ScreenFullSpecResponse.DataFieldSummaryEntry dataFieldEntry = null;
        if (field.dataEntityFieldId() != null && dataFieldsById.containsKey(field.dataEntityFieldId())) {
            RegistryDataEntityField df = dataFieldsById.get(field.dataEntityFieldId());
            dataFieldEntry = new ScreenFullSpecResponse.DataFieldSummaryEntry(df.id(), df.columnName(), df.dataType(),
                    df.maxLength(), df.isNullable(), df.isUnique());
        }

        // Mode configs
        List<ScreenFullSpecResponse.ModeConfigEntry> modeConfigEntries = configsByFieldId
                .getOrDefault(field.id(), List.of()).stream()
                .filter(c -> modesById.containsKey(c.modeId()) && "ACTIVE".equals(modesById.get(c.modeId()).status().name()))
                .sorted(Comparator.comparingInt(c -> modesById.get(c.modeId()).displayOrder()))
                .map(c -> new ScreenFullSpecResponse.ModeConfigEntry(
                        c.modeId(), modesById.get(c.modeId()).modeCode(),
                        c.isVisible(), c.isRequired(), c.isReadonly(), c.defaultValue(), c.displayOrder()))
                .toList();

        // Validations
        List<ScreenFullSpecResponse.ValidationEntry> validationEntries = validationsByFieldId
                .getOrDefault(field.id(), List.of()).stream()
                .sorted(Comparator.comparingInt(RegistryScreenFieldValidation::displayOrder))
                .map(v -> {
                    String modeCode = v.modeId() != null && modesById.containsKey(v.modeId())
                            ? modesById.get(v.modeId()).modeCode() : null;
                    String ruleTypeCode = ruleTypesById.containsKey(v.ruleTypeId())
                            ? ruleTypesById.get(v.ruleTypeId()).code() : null;
                    return new ScreenFullSpecResponse.ValidationEntry(v.id(), v.modeId(), modeCode,
                            ruleTypeCode, v.ruleParamJson(), v.conditionJson(), v.errorMessage(), v.remark(), v.displayOrder());
                })
                .toList();

        return new FieldExportEntry(field.id(), field.sectionId(), field.fieldKey(), field.label(),
                field.fieldType(), field.description(), field.required(), field.displayOrder(),
                field.maxLength(), field.remark(), componentEntry, dataFieldEntry, modeConfigEntries, validationEntries);
    }
}

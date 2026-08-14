package com.company.scopery.modules.traceability.screen.application.action;

import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponent;
import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponentRepository;
import com.company.scopery.modules.traceability.fieldmodeconfig.domain.model.RegistryScreenFieldModeConfig;
import com.company.scopery.modules.traceability.fieldmodeconfig.domain.model.RegistryScreenFieldModeConfigRepository;
import com.company.scopery.modules.traceability.fieldvalidation.domain.model.RegistryScreenFieldValidation;
import com.company.scopery.modules.traceability.fieldvalidation.domain.model.RegistryScreenFieldValidationRepository;
import com.company.scopery.modules.traceability.screen.application.command.ImportFullScreenSpecItemCommand;
import com.company.scopery.modules.traceability.screen.application.response.RegistryScreenResponse;
import com.company.scopery.modules.traceability.screen.domain.model.RegistryScreen;
import com.company.scopery.modules.traceability.screen.domain.model.RegistryScreenRepository;
import com.company.scopery.modules.traceability.screeneventitem.domain.model.RegistryScreenEventItem;
import com.company.scopery.modules.traceability.screeneventitem.domain.model.RegistryScreenEventItemRepository;
import com.company.scopery.modules.traceability.screenfield.domain.model.RegistryScreenField;
import com.company.scopery.modules.traceability.screenfield.domain.model.RegistryScreenFieldRepository;
import com.company.scopery.modules.traceability.screenmode.domain.model.RegistryScreenMode;
import com.company.scopery.modules.traceability.screenmode.domain.model.RegistryScreenModeRepository;
import com.company.scopery.modules.traceability.screenprocessitem.domain.model.RegistryScreenProcessItem;
import com.company.scopery.modules.traceability.screenprocessitem.domain.model.RegistryScreenProcessItemRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.validationruletype.domain.model.RegistryValidationRuleType;
import com.company.scopery.modules.traceability.validationruletype.domain.model.RegistryValidationRuleTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class ImportFullScreenSpecAction {

    private static final Logger log = LoggerFactory.getLogger(ImportFullScreenSpecAction.class);

    private final RegistryScreenRepository screenRepo;
    private final RegistryScreenModeRepository modeRepo;
    private final RegistryScreenFieldRepository fieldRepo;
    private final RegistryScreenFieldModeConfigRepository modeConfigRepo;
    private final RegistryScreenFieldValidationRepository validationRepo;
    private final RegistryScreenProcessItemRepository processItemRepo;
    private final RegistryScreenEventItemRepository eventItemRepo;
    private final RegistryAppComponentRepository componentRepo;
    private final RegistryValidationRuleTypeRepository ruleTypeRepo;
    private final TraceabilityAuthorizationService authorization;

    public ImportFullScreenSpecAction(RegistryScreenRepository screenRepo,
                                      RegistryScreenModeRepository modeRepo,
                                      RegistryScreenFieldRepository fieldRepo,
                                      RegistryScreenFieldModeConfigRepository modeConfigRepo,
                                      RegistryScreenFieldValidationRepository validationRepo,
                                      RegistryScreenProcessItemRepository processItemRepo,
                                      RegistryScreenEventItemRepository eventItemRepo,
                                      RegistryAppComponentRepository componentRepo,
                                      RegistryValidationRuleTypeRepository ruleTypeRepo,
                                      TraceabilityAuthorizationService authorization) {
        this.screenRepo = screenRepo;
        this.modeRepo = modeRepo;
        this.fieldRepo = fieldRepo;
        this.modeConfigRepo = modeConfigRepo;
        this.validationRepo = validationRepo;
        this.processItemRepo = processItemRepo;
        this.eventItemRepo = eventItemRepo;
        this.componentRepo = componentRepo;
        this.ruleTypeRepo = ruleTypeRepo;
        this.authorization = authorization;
    }

    @Transactional
    public RegistryScreenResponse execute(ImportFullScreenSpecItemCommand cmd) {
        authorization.requireWorkspaceCreate(cmd.workspaceId());

        RegistryScreen screen = screenRepo.save(
                RegistryScreen.create(cmd.applicationId(), cmd.projectId(),
                        cmd.code().trim(), cmd.name().trim(), cmd.routePath()));

        Map<String, RegistryScreenMode> modeByCode = createModes(screen.id(), cmd);

        Map<String, RegistryAppComponent> componentByCode = preloadComponents(cmd);
        Map<String, RegistryValidationRuleType> ruleTypeByCode = preloadRuleTypes(cmd);
        Map<String, UUID> screenIdByCode = preloadTargetScreens(screen, cmd);

        Map<String, RegistryScreenField> fieldByKey = createFieldsWithSubEntities(
                screen.id(), cmd, modeByCode, componentByCode, ruleTypeByCode);

        createProcessItems(screen.id(), cmd, modeByCode, fieldByKey);
        createEventItems(screen.id(), cmd, modeByCode, fieldByKey, screenIdByCode);

        log.info("[ImportFullScreenSpec] Imported — id={} code={} modes={} fields={} processItems={} eventItems={}",
                screen.id(), screen.code(), modeByCode.size(), fieldByKey.size(),
                cmd.processItems() != null ? cmd.processItems().size() : 0,
                cmd.eventItems() != null ? cmd.eventItems().size() : 0);

        return RegistryScreenResponse.from(screen);
    }

    private Map<String, RegistryScreenMode> createModes(UUID screenId, ImportFullScreenSpecItemCommand cmd) {
        Map<String, RegistryScreenMode> modeByCode = new LinkedHashMap<>();
        if (cmd.modes() == null) return modeByCode;
        for (var m : cmd.modes()) {
            RegistryScreenMode mode = modeRepo.save(
                    RegistryScreenMode.create(screenId, cmd.workspaceId(), m.modeCode(), m.name().trim(), m.displayOrder()));
            modeByCode.put(m.modeCode(), mode);
        }
        return modeByCode;
    }

    private Map<String, RegistryAppComponent> preloadComponents(ImportFullScreenSpecItemCommand cmd) {
        Set<String> codes = new HashSet<>();
        if (cmd.fields() != null) {
            cmd.fields().stream()
                    .map(ImportFullScreenSpecItemCommand.FieldItem::componentCode)
                    .filter(Objects::nonNull)
                    .forEach(codes::add);
        }
        if (codes.isEmpty()) return Map.of();
        Map<String, RegistryAppComponent> result = new HashMap<>();
        componentRepo.findByApplicationId(cmd.applicationId()).stream()
                .filter(c -> codes.contains(c.code()))
                .forEach(c -> result.put(c.code(), c));
        return result;
    }

    private Map<String, RegistryValidationRuleType> preloadRuleTypes(ImportFullScreenSpecItemCommand cmd) {
        Set<String> codes = new HashSet<>();
        if (cmd.fields() != null) {
            cmd.fields().stream()
                    .filter(f -> f.validations() != null)
                    .flatMap(f -> f.validations().stream())
                    .map(ImportFullScreenSpecItemCommand.FieldItem.ValidationItem::ruleTypeCode)
                    .filter(Objects::nonNull)
                    .forEach(codes::add);
        }
        if (codes.isEmpty()) return Map.of();
        Map<String, RegistryValidationRuleType> result = new HashMap<>();
        ruleTypeRepo.findAllAccessible(cmd.workspaceId()).stream()
                .filter(rt -> codes.contains(rt.code()))
                .forEach(rt -> result.put(rt.code(), rt));
        return result;
    }

    private Map<String, UUID> preloadTargetScreens(RegistryScreen newScreen, ImportFullScreenSpecItemCommand cmd) {
        Set<String> codes = new HashSet<>();
        if (cmd.eventItems() != null) {
            cmd.eventItems().stream()
                    .map(ImportFullScreenSpecItemCommand.EventItem::targetScreenCode)
                    .filter(Objects::nonNull)
                    .forEach(codes::add);
        }
        Map<String, UUID> result = new HashMap<>();
        result.put(newScreen.code(), newScreen.id());
        if (!codes.isEmpty()) {
            screenRepo.findByApplicationId(cmd.applicationId()).stream()
                    .filter(s -> codes.contains(s.code()))
                    .forEach(s -> result.put(s.code(), s.id()));
        }
        return result;
    }

    private Map<String, RegistryScreenField> createFieldsWithSubEntities(
            UUID screenId,
            ImportFullScreenSpecItemCommand cmd,
            Map<String, RegistryScreenMode> modeByCode,
            Map<String, RegistryAppComponent> componentByCode,
            Map<String, RegistryValidationRuleType> ruleTypeByCode) {

        Map<String, RegistryScreenField> fieldByKey = new LinkedHashMap<>();
        if (cmd.fields() == null) return fieldByKey;

        for (var f : cmd.fields()) {
            UUID componentId = resolveComponentId(f, componentByCode, cmd.code());
            RegistryScreenField field = fieldRepo.save(
                    RegistryScreenField.create(screenId, null, cmd.workspaceId(),
                            f.fieldKey(), f.label(), f.fieldType(), f.description(),
                            f.required(), f.displayOrder(), componentId, null, f.maxLength(), f.remark()));
            fieldByKey.put(f.fieldKey(), field);
            createModeConfigs(field.id(), f, modeByCode, cmd.workspaceId());
            createValidations(field.id(), f, modeByCode, ruleTypeByCode, cmd.workspaceId());
        }
        return fieldByKey;
    }

    private UUID resolveComponentId(ImportFullScreenSpecItemCommand.FieldItem f,
                                     Map<String, RegistryAppComponent> componentByCode,
                                     String screenCode) {
        if (f.componentCode() == null) return null;
        RegistryAppComponent comp = componentByCode.get(f.componentCode());
        if (comp == null) {
            log.warn("[ImportFullScreenSpec] componentCode='{}' not found for field='{}' in screen='{}', skipping link",
                    f.componentCode(), f.fieldKey(), screenCode);
        }
        return comp != null ? comp.id() : null;
    }

    private void createModeConfigs(UUID fieldId,
                                   ImportFullScreenSpecItemCommand.FieldItem f,
                                   Map<String, RegistryScreenMode> modeByCode,
                                   UUID workspaceId) {
        if (f.modeConfigs() == null || f.modeConfigs().isEmpty()) return;
        List<RegistryScreenFieldModeConfig> configs = new ArrayList<>();
        for (var mc : f.modeConfigs()) {
            RegistryScreenMode mode = modeByCode.get(mc.modeCode());
            if (mode == null) {
                log.warn("[ImportFullScreenSpec] modeCode='{}' not found for field='{}' modeConfig, skipping",
                        mc.modeCode(), f.fieldKey());
                continue;
            }
            configs.add(RegistryScreenFieldModeConfig.create(
                    fieldId, mode.id(), workspaceId,
                    mc.isVisible(), mc.isRequired(), mc.isReadonly(),
                    mc.defaultValue(), mc.displayOrder()));
        }
        if (!configs.isEmpty()) modeConfigRepo.saveAll(configs);
    }

    private void createValidations(UUID fieldId,
                                   ImportFullScreenSpecItemCommand.FieldItem f,
                                   Map<String, RegistryScreenMode> modeByCode,
                                   Map<String, RegistryValidationRuleType> ruleTypeByCode,
                                   UUID workspaceId) {
        if (f.validations() == null) return;
        for (var v : f.validations()) {
            RegistryValidationRuleType ruleType = ruleTypeByCode.get(v.ruleTypeCode());
            if (ruleType == null) {
                log.warn("[ImportFullScreenSpec] ruleTypeCode='{}' not found for field='{}', skipping validation",
                        v.ruleTypeCode(), f.fieldKey());
                continue;
            }
            UUID modeId = v.modeCode() != null
                    ? Optional.ofNullable(modeByCode.get(v.modeCode())).map(RegistryScreenMode::id).orElse(null)
                    : null;
            validationRepo.save(RegistryScreenFieldValidation.create(
                    fieldId, modeId, ruleType.id(), workspaceId,
                    v.ruleParamJson(), v.conditionJson(), v.errorMessage(), v.remark(), v.displayOrder()));
        }
    }

    private void createProcessItems(UUID screenId,
                                    ImportFullScreenSpecItemCommand cmd,
                                    Map<String, RegistryScreenMode> modeByCode,
                                    Map<String, RegistryScreenField> fieldByKey) {
        if (cmd.processItems() == null) return;
        for (var p : cmd.processItems()) {
            UUID modeId = p.modeCode() != null
                    ? Optional.ofNullable(modeByCode.get(p.modeCode())).map(RegistryScreenMode::id).orElse(null)
                    : null;
            UUID targetFieldId = p.targetFieldKey() != null
                    ? Optional.ofNullable(fieldByKey.get(p.targetFieldKey())).map(RegistryScreenField::id).orElse(null)
                    : null;
            processItemRepo.save(RegistryScreenProcessItem.create(
                    screenId, cmd.workspaceId(), modeId, targetFieldId,
                    p.title(), p.content(), p.sourceTable(), p.conditionNote(), p.displayOrder()));
        }
    }

    private void createEventItems(UUID screenId,
                                  ImportFullScreenSpecItemCommand cmd,
                                  Map<String, RegistryScreenMode> modeByCode,
                                  Map<String, RegistryScreenField> fieldByKey,
                                  Map<String, UUID> screenIdByCode) {
        if (cmd.eventItems() == null) return;
        for (var e : cmd.eventItems()) {
            UUID modeId = e.modeCode() != null
                    ? Optional.ofNullable(modeByCode.get(e.modeCode())).map(RegistryScreenMode::id).orElse(null)
                    : null;
            UUID triggerFieldId = e.triggerFieldKey() != null
                    ? Optional.ofNullable(fieldByKey.get(e.triggerFieldKey())).map(RegistryScreenField::id).orElse(null)
                    : null;
            UUID targetScreenId = e.targetScreenCode() != null
                    ? screenIdByCode.get(e.targetScreenCode())
                    : null;
            eventItemRepo.save(RegistryScreenEventItem.create(
                    screenId, cmd.workspaceId(), modeId, triggerFieldId,
                    e.triggerActionCode(), e.title(), e.content(), e.conditionNote(),
                    targetScreenId, e.targetModeCode(), e.displayOrder()));
        }
    }
}

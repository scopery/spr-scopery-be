package com.company.scopery.modules.traceability.fieldmodeconfig.application.action;

import com.company.scopery.modules.traceability.fieldmodeconfig.application.command.ReplaceScreenFieldModeConfigsCommand;
import com.company.scopery.modules.traceability.fieldmodeconfig.application.command.ReplaceScreenFieldModeConfigsCommand.ModeConfigItem;
import com.company.scopery.modules.traceability.fieldmodeconfig.application.response.RegistryScreenFieldModeConfigResponse;
import com.company.scopery.modules.traceability.fieldmodeconfig.domain.model.RegistryScreenFieldModeConfig;
import com.company.scopery.modules.traceability.fieldmodeconfig.domain.model.RegistryScreenFieldModeConfigRepository;
import com.company.scopery.modules.traceability.screenfield.infrastructure.persistence.SpringDataRegistryScreenFieldJpaRepository;
import com.company.scopery.modules.traceability.screenmode.domain.model.RegistryScreenMode;
import com.company.scopery.modules.traceability.screenmode.domain.model.RegistryScreenModeRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ReplaceScreenFieldModeConfigsAction {

    private final RegistryScreenFieldModeConfigRepository modeConfigRepo;
    private final SpringDataRegistryScreenFieldJpaRepository fieldSpringData;
    private final RegistryScreenModeRepository modeRepo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public ReplaceScreenFieldModeConfigsAction(
            RegistryScreenFieldModeConfigRepository modeConfigRepo,
            SpringDataRegistryScreenFieldJpaRepository fieldSpringData,
            RegistryScreenModeRepository modeRepo,
            TraceabilityAuthorizationService authorization,
            TraceabilityActivityLogger activityLogger) {
        this.modeConfigRepo = modeConfigRepo;
        this.fieldSpringData = fieldSpringData;
        this.modeRepo = modeRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public List<RegistryScreenFieldModeConfigResponse> execute(ReplaceScreenFieldModeConfigsCommand command) {
        if (command.modeConfigs().isEmpty()) {
            throw TraceabilityExceptions.modeConfigPayloadEmpty();
        }

        authorization.requireWorkspaceCreate(command.workspaceId());

        // Pessimistic lock on the field + IDOR check (screenId + workspaceId)
        fieldSpringData.lockFieldByIdAndScreenIdAndWorkspaceId(
                        command.fieldId(), command.screenId(), command.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.screenFieldNotFound(command.fieldId()));

        // Batch load all mode IDs we need: payload + existing
        Set<UUID> payloadModeIds = command.modeConfigs().stream()
                .map(ModeConfigItem::modeId)
                .collect(Collectors.toSet());

        List<RegistryScreenFieldModeConfig> existingConfigs = modeConfigRepo.findByFieldId(command.fieldId());
        Set<UUID> existingModeIds = existingConfigs.stream()
                .map(RegistryScreenFieldModeConfig::modeId)
                .collect(Collectors.toSet());

        Set<UUID> allModeIds = new HashSet<>(payloadModeIds);
        allModeIds.addAll(existingModeIds);

        Map<UUID, RegistryScreenMode> modesMap = modeRepo.findByIdIn(allModeIds).stream()
                .collect(Collectors.toMap(RegistryScreenMode::id, m -> m));

        // Validate each payload mode (batch — no per-item queries)
        for (ModeConfigItem item : command.modeConfigs()) {
            RegistryScreenMode mode = Optional.ofNullable(modesMap.get(item.modeId()))
                    .orElseThrow(() -> TraceabilityExceptions.screenModeNotFound(item.modeId()));
            if (!mode.workspaceId().equals(command.workspaceId())) {
                throw TraceabilityExceptions.screenModeNotFound(item.modeId());
            }
            if (!mode.screenId().equals(command.screenId())) {
                throw TraceabilityExceptions.screenModeWrongScreen(item.modeId());
            }
            if (!mode.status().name().equals("ACTIVE")) {
                throw TraceabilityExceptions.screenModeInactive(item.modeId());
            }
        }

        // UPSERT: merge payload with existing configs
        Map<UUID, RegistryScreenFieldModeConfig> existingByModeId = existingConfigs.stream()
                .collect(Collectors.toMap(RegistryScreenFieldModeConfig::modeId, c -> c));

        List<RegistryScreenFieldModeConfig> toSave = command.modeConfigs().stream()
                .map(item -> {
                    RegistryScreenFieldModeConfig existing = existingByModeId.get(item.modeId());
                    if (existing != null) {
                        return existing.withUpdated(item.isVisible(), item.isRequired(),
                                item.isReadonly(), item.defaultValue(), item.displayOrder());
                    } else {
                        return RegistryScreenFieldModeConfig.create(
                                command.fieldId(), item.modeId(), command.workspaceId(),
                                item.isVisible(), item.isRequired(), item.isReadonly(),
                                item.defaultValue(), item.displayOrder());
                    }
                })
                .toList();

        List<RegistryScreenFieldModeConfig> saved = modeConfigRepo.saveAll(toSave);

        // Delete stale configs: existing configs for ACTIVE modes not in payload
        List<UUID> toDelete = existingConfigs.stream()
                .filter(c -> !payloadModeIds.contains(c.modeId()))
                .filter(c -> {
                    RegistryScreenMode m = modesMap.get(c.modeId());
                    return m != null && m.status().name().equals("ACTIVE");
                })
                .map(RegistryScreenFieldModeConfig::id)
                .toList();

        if (!toDelete.isEmpty()) {
            modeConfigRepo.deleteByIdIn(toDelete);
        }

        activityLogger.logSuccess(TraceabilityEntityTypes.SCREEN_FIELD_MODE_CONFIG, command.fieldId(),
                TraceabilityActivityActions.FIELD_MODE_CONFIGS_REPLACED,
                "Field mode configs replaced for field: " + command.fieldId());

        return saved.stream().map(RegistryScreenFieldModeConfigResponse::from).toList();
    }
}

package com.company.scopery.modules.specpack.pack.application.action;

import com.company.scopery.modules.specpack.pack.application.command.CreateSpecPackCommand;
import com.company.scopery.modules.specpack.pack.application.response.SpecPackResponse;
import com.company.scopery.modules.specpack.pack.domain.enums.SpecPackType;
import com.company.scopery.modules.specpack.pack.domain.model.SpecPack;
import com.company.scopery.modules.specpack.pack.domain.model.SpecPackRepository;
import com.company.scopery.modules.specpack.shared.activity.SpecPackActivityLogger;
import com.company.scopery.modules.specpack.shared.constant.SpecPackActivityActions;
import com.company.scopery.modules.specpack.shared.constant.SpecPackEntityTypes;
import com.company.scopery.modules.specpack.shared.util.SpecPackEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateSpecPackAction {

    private final SpecPackRepository repository;
    private final SpecPackActivityLogger activityLogger;

    public CreateSpecPackAction(SpecPackRepository repository, SpecPackActivityLogger activityLogger) {
        this.repository = repository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public SpecPackResponse execute(CreateSpecPackCommand command) {
        SpecPackType packType = SpecPackEnumParser.parseRequired(SpecPackType.class, command.packType(), "packType");

        SpecPack pack = SpecPack.create(
                command.projectId(),
                packType,
                command.name(),
                command.description(),
                command.sourcePackId()
        );

        SpecPack saved = repository.save(pack);

        activityLogger.logSuccess(
                SpecPackEntityTypes.SPEC_PACK,
                saved.id(),
                SpecPackActivityActions.SPEC_PACK_CREATED,
                "Spec pack created: " + saved.name()
        );

        return SpecPackResponse.from(saved);
    }
}

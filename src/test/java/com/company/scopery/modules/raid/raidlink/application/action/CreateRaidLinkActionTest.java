package com.company.scopery.modules.raid.raidlink.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.raid.raiditem.domain.enums.RaidItemType;
import com.company.scopery.modules.raid.raiditem.domain.model.RaidItem;
import com.company.scopery.modules.raid.raiditem.domain.model.RaidItemRepository;
import com.company.scopery.modules.raid.raidlink.application.command.CreateRaidLinkCommand;
import com.company.scopery.modules.raid.raidlink.domain.model.RaidLinkRepository;
import com.company.scopery.modules.raid.shared.activity.RaidActivityLogger;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.error.RaidErrorCatalog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateRaidLinkActionTest {

    @Mock RaidItemRepository items;
    @Mock RaidLinkRepository links;
    @Mock RaidAuthorizationService authorization;
    @Mock RaidActivityLogger activityLogger;

    CreateRaidLinkAction action;

    final UUID projectId = UUID.randomUUID();
    final UUID raidItemId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        action = new CreateRaidLinkAction(items, links, authorization, activityLogger);
    }

    @Test
    void execute_rejectsBlankLinkType() {
        RaidItem item = RaidItem.create(projectId, UUID.randomUUID(), RaidItemType.RISK, "R-1", "Risk", null, null);
        when(items.findByIdAndProjectId(raidItemId, projectId)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> action.execute(new CreateRaidLinkCommand(
                projectId, raidItemId, "  ", "TASK", UUID.randomUUID())))
                .isInstanceOf(AppException.class)
                .extracting(ex -> ((AppException) ex).getErrorCode())
                .isEqualTo(RaidErrorCatalog.RAID_LINK_TYPE_REQUIRED.code());
    }
}

package com.company.scopery.modules.raid.raiditem.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.raid.raiditem.domain.enums.RaidItemType;
import com.company.scopery.modules.raid.raiditem.domain.model.RaidItem;
import com.company.scopery.modules.raid.raiditem.domain.model.RaidItemRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConvertRiskToIssueActionTest {

    @Mock RaidItemRepository items;
    @Mock RaidAuthorizationService authorization;
    @Mock RaidActivityLogger activityLogger;

    ConvertRiskToIssueAction action;

    final UUID projectId = UUID.randomUUID();
    final UUID itemId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        action = new ConvertRiskToIssueAction(items, authorization, activityLogger);
    }

    @Test
    void execute_convertsRiskToIssue() {
        RaidItem risk = RaidItem.create(projectId, UUID.randomUUID(), RaidItemType.RISK, "R-1", "Risk", null, null);
        when(items.findByIdAndProjectId(itemId, projectId)).thenReturn(Optional.of(risk));
        when(items.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var response = action.execute(projectId, itemId);

        assertThat(response.type()).isEqualTo(RaidItemType.ISSUE.name());
        verify(authorization).requireConvert(projectId);
        verify(items).save(any());
    }

    @Test
    void execute_rejectsNonRiskWithInvalidStatus() {
        RaidItem issue = RaidItem.create(projectId, UUID.randomUUID(), RaidItemType.ISSUE, "I-1", "Issue", null, null);
        when(items.findByIdAndProjectId(itemId, projectId)).thenReturn(Optional.of(issue));

        assertThatThrownBy(() -> action.execute(projectId, itemId))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(RaidErrorCatalog.RAID_INVALID_STATUS.code()));
        verify(items, never()).save(any());
    }
}

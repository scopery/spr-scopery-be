package com.company.scopery.modules.ratecard.ratecardversion.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.ratecard.costrole.domain.enums.CostRoleScope;
import com.company.scopery.modules.ratecard.costrole.domain.enums.CostRoleStatus;
import com.company.scopery.modules.ratecard.costrole.domain.model.CostRole;
import com.company.scopery.modules.ratecard.costrole.domain.model.CostRoleRepository;
import com.company.scopery.modules.ratecard.ratecard.domain.enums.RateCardScope;
import com.company.scopery.modules.ratecard.ratecard.domain.model.RateCard;
import com.company.scopery.modules.ratecard.ratecard.domain.model.RateCardRepository;
import com.company.scopery.modules.ratecard.ratecardline.domain.model.RateCardLine;
import com.company.scopery.modules.ratecard.ratecardline.domain.model.RateCardLineRepository;
import com.company.scopery.modules.ratecard.ratecardversion.application.command.PublishRateCardVersionCommand;
import com.company.scopery.modules.ratecard.ratecardversion.application.command.UpdateRateCardVersionCommand;
import com.company.scopery.modules.ratecard.ratecardversion.domain.enums.RateCardVersionStatus;
import com.company.scopery.modules.ratecard.ratecardversion.domain.model.RateCardVersion;
import com.company.scopery.modules.ratecard.ratecardversion.domain.model.RateCardVersionRepository;
import com.company.scopery.modules.ratecard.shared.activity.RateCardActivityLogger;
import com.company.scopery.modules.ratecard.shared.authorization.RateCardAuthorizationService;
import com.company.scopery.modules.ratecard.shared.error.RateCardErrorCatalog;
import com.company.scopery.modules.ratecard.shared.support.RateCardPlatformPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateCardVersionPublishActionTest {

    @Mock RateCardRepository rateCardRepository;
    @Mock RateCardVersionRepository versionRepository;
    @Mock RateCardLineRepository lineRepository;
    @Mock CostRoleRepository costRoleRepository;
    @Mock RateCardAuthorizationService authorizationService;
    @Mock RateCardActivityLogger activityLogger;
    @Mock RateCardPlatformPublisher platformPublisher;

    PublishRateCardVersionAction publishAction;
    UpdateRateCardVersionAction updateAction;

    UUID workspaceId = UUID.randomUUID();
    RateCard card;
    RateCardVersion draft;
    CostRole role;

    @BeforeEach
    void setUp() {
        publishAction = new PublishRateCardVersionAction(rateCardRepository, versionRepository, lineRepository,
                costRoleRepository, authorizationService, activityLogger, platformPublisher);
        updateAction = new UpdateRateCardVersionAction(rateCardRepository, versionRepository,
                authorizationService, activityLogger, platformPublisher);
        card = RateCard.create("WS_DEFAULT", "Default", null, RateCardScope.WORKSPACE,
                UUID.randomUUID(), workspaceId, "USD", true);
        draft = RateCardVersion.create(card.id(), 1, "v1", null, LocalDate.of(2026, 1, 1), null);
        role = CostRole.create("BACKEND_DEVELOPER", "Backend", null, CostRoleScope.SYSTEM, null, null, "ENG", true);
    }

    @Test
    void publish_noLines_rejected() {
        when(rateCardRepository.findById(card.id())).thenReturn(Optional.of(card));
        when(versionRepository.findById(draft.id())).thenReturn(Optional.of(draft));
        when(lineRepository.findByVersionId(draft.id())).thenReturn(List.of());

        assertThatThrownBy(() -> publishAction.execute(new PublishRateCardVersionCommand(card.id(), draft.id())))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(RateCardErrorCatalog.RATE_CARD_VERSION_NO_LINES.code()));
    }

    @Test
    void publish_success_setsPublishedAndCurrentVersion() {
        RateCardLine line = RateCardLine.create(draft.id(), role.id(), null, null, "USD",
                new BigDecimal("100.0000"), null, null);
        when(rateCardRepository.findById(card.id())).thenReturn(Optional.of(card));
        when(versionRepository.findById(draft.id())).thenReturn(Optional.of(draft));
        when(lineRepository.findByVersionId(draft.id())).thenReturn(List.of(line));
        when(costRoleRepository.findById(role.id())).thenReturn(Optional.of(role));
        when(versionRepository.existsPublishedOverlap(any(), any(), any(), any())).thenReturn(false);
        when(versionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(rateCardRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(authorizationService.currentUserId()).thenReturn(UUID.randomUUID());

        var result = publishAction.execute(new PublishRateCardVersionCommand(card.id(), draft.id()));
        assertThat(result.status()).isEqualTo(RateCardVersionStatus.PUBLISHED.name());
        assertThat(result.publishedAt()).isNotNull();
    }

    @Test
    void update_published_rejected() {
        RateCardVersion published = draft.publish(UUID.randomUUID());
        when(rateCardRepository.findById(card.id())).thenReturn(Optional.of(card));
        when(versionRepository.findById(published.id())).thenReturn(Optional.of(published));

        assertThatThrownBy(() -> updateAction.execute(new UpdateRateCardVersionCommand(
                card.id(), published.id(), "x", null, LocalDate.of(2026, 1, 1), null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(RateCardErrorCatalog.RATE_CARD_VERSION_NOT_DRAFT.code()));
    }

    @Test
    void publish_overlap_rejected() {
        RateCardLine line = RateCardLine.create(draft.id(), role.id(), null, null, "USD",
                new BigDecimal("50.0000"), null, null);
        when(rateCardRepository.findById(card.id())).thenReturn(Optional.of(card));
        when(versionRepository.findById(draft.id())).thenReturn(Optional.of(draft));
        when(lineRepository.findByVersionId(draft.id())).thenReturn(List.of(line));
        when(costRoleRepository.findById(role.id())).thenReturn(Optional.of(role));
        when(versionRepository.existsPublishedOverlap(any(), any(), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> publishAction.execute(new PublishRateCardVersionCommand(card.id(), draft.id())))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(RateCardErrorCatalog.RATE_CARD_VERSION_OVERLAP.code()));
    }
}

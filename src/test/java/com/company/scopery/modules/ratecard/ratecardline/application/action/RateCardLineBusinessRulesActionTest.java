package com.company.scopery.modules.ratecard.ratecardline.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.ratecard.costrole.domain.enums.CostRoleScope;
import com.company.scopery.modules.ratecard.costrole.domain.model.CostRole;
import com.company.scopery.modules.ratecard.costrole.domain.model.CostRoleRepository;
import com.company.scopery.modules.ratecard.ratecard.domain.enums.RateCardScope;
import com.company.scopery.modules.ratecard.ratecard.domain.model.RateCard;
import com.company.scopery.modules.ratecard.ratecard.domain.model.RateCardRepository;
import com.company.scopery.modules.ratecard.ratecardline.application.command.CreateRateCardLineCommand;
import com.company.scopery.modules.ratecard.ratecardline.domain.model.RateCardLineRepository;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateCardLineBusinessRulesActionTest {

    @Mock RateCardRepository rateCardRepository;
    @Mock RateCardVersionRepository versionRepository;
    @Mock RateCardLineRepository lineRepository;
    @Mock CostRoleRepository costRoleRepository;
    @Mock RateCardAuthorizationService authorizationService;
    @Mock RateCardActivityLogger activityLogger;
    @Mock RateCardPlatformPublisher platformPublisher;

    CreateRateCardLineAction createAction;
    RateCard card;
    RateCardVersion draft;
    CostRole role;

    @BeforeEach
    void setUp() {
        createAction = new CreateRateCardLineAction(rateCardRepository, versionRepository, lineRepository,
                costRoleRepository, authorizationService, activityLogger, platformPublisher);
        card = RateCard.create("CARD", "Card", null, RateCardScope.WORKSPACE, null, UUID.randomUUID(), "USD", false);
        draft = RateCardVersion.create(card.id(), 1, null, null, LocalDate.now(), null);
        role = CostRole.create("BACKEND_DEVELOPER", "Backend", null, CostRoleScope.SYSTEM, null, null, null, true);
    }

    @Test
    void create_invalidCostRate_rejected() {
        when(rateCardRepository.findById(card.id())).thenReturn(Optional.of(card));
        when(versionRepository.findById(draft.id())).thenReturn(Optional.of(draft));

        assertThatThrownBy(() -> createAction.execute(new CreateRateCardLineCommand(
                card.id(), draft.id(), role.id(), null, null, "USD", BigDecimal.ZERO, null, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(RateCardErrorCatalog.RATE_CARD_LINE_INVALID_COST_RATE.code()));
    }

    @Test
    void create_invalidCurrency_rejected() {
        when(rateCardRepository.findById(card.id())).thenReturn(Optional.of(card));
        when(versionRepository.findById(draft.id())).thenReturn(Optional.of(draft));

        assertThatThrownBy(() -> createAction.execute(new CreateRateCardLineCommand(
                card.id(), draft.id(), role.id(), null, null, "XXX", new BigDecimal("10"), null, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(RateCardErrorCatalog.RATE_CARD_LINE_INVALID_CURRENCY.code()));
    }

    @Test
    void create_publishedVersion_rejected() {
        RateCardVersion published = draft.publish(UUID.randomUUID());
        when(rateCardRepository.findById(card.id())).thenReturn(Optional.of(card));
        when(versionRepository.findById(published.id())).thenReturn(Optional.of(published));

        assertThatThrownBy(() -> createAction.execute(new CreateRateCardLineCommand(
                card.id(), published.id(), role.id(), null, null, "USD", new BigDecimal("10"), null, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(RateCardErrorCatalog.RATE_CARD_VERSION_NOT_DRAFT.code()));
    }

    @Test
    void create_duplicateLine_rejected() {
        when(rateCardRepository.findById(card.id())).thenReturn(Optional.of(card));
        when(versionRepository.findById(draft.id())).thenReturn(Optional.of(draft));
        when(costRoleRepository.findById(role.id())).thenReturn(Optional.of(role));
        when(lineRepository.existsDuplicate(any(), any(), any(), any(), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> createAction.execute(new CreateRateCardLineCommand(
                card.id(), draft.id(), role.id(), "SENIOR", null, "USD", new BigDecimal("10"), null, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(RateCardErrorCatalog.RATE_CARD_LINE_DUPLICATE.code()));
    }
}

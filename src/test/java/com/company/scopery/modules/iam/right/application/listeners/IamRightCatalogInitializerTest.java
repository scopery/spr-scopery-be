package com.company.scopery.modules.iam.right.application.listeners;

import com.company.scopery.modules.iam.right.domain.model.IamRightRepository;
import com.company.scopery.modules.iam.right.domain.valueobject.IamRightCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IamRightCatalogInitializerTest {

    @Mock
    private IamRightRepository rightRepository;

    @Mock
    private ApplicationReadyEvent event;

    private IamRightCatalogInitializer initializer;

    @BeforeEach
    void setUp() {
        initializer = new IamRightCatalogInitializer(rightRepository);
    }

    @Test
    void onApplicationEvent_allNew_seeds24Rights() {
        when(rightRepository.existsByCode(any(IamRightCode.class))).thenReturn(false);
        when(rightRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        initializer.onApplicationEvent(event);

        verify(rightRepository, atLeast(20)).save(any());
    }

    @Test
    void onApplicationEvent_allExist_seedsNothing() {
        when(rightRepository.existsByCode(any(IamRightCode.class))).thenReturn(true);

        initializer.onApplicationEvent(event);

        verify(rightRepository, never()).save(any());
    }

    @Test
    void onApplicationEvent_idempotent_skipsExistingRights() {
        when(rightRepository.existsByCode(any(IamRightCode.class))).thenReturn(false);
        when(rightRepository.existsByCode(IamRightCode.of("VIEW_ORGANIZATION"))).thenReturn(true);
        when(rightRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        initializer.onApplicationEvent(event);

        verify(rightRepository, atLeast(1)).save(any());
    }
}

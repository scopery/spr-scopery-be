package com.company.scopery.modules.iam.right.application.listeners;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IamRightCatalogInitializerTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private ApplicationReadyEvent event;

    private IamRightCatalogInitializer initializer;

    @BeforeEach
    void setUp() {
        initializer = new IamRightCatalogInitializer(jdbcTemplate);
    }

    @Test
    void onApplicationEvent_seedsRightsViaUpsert() {
        when(jdbcTemplate.update(anyString(), any(Object[].class))).thenReturn(1);

        initializer.onApplicationEvent(event);

        verify(jdbcTemplate, atLeast(20)).update(anyString(), any(Object[].class));
    }

    @Test
    void onApplicationEvent_allExist_noRowsInserted() {
        when(jdbcTemplate.update(anyString(), any(Object[].class))).thenReturn(0);

        initializer.onApplicationEvent(event);

        verify(jdbcTemplate, atLeast(1)).update(anyString(), any(Object[].class));
    }

    @Test
    void onApplicationEvent_idempotent_partialInsert() {
        when(jdbcTemplate.update(anyString(), any(Object[].class)))
                .thenReturn(1)
                .thenReturn(0);

        initializer.onApplicationEvent(event);

        verify(jdbcTemplate, atLeast(2)).update(anyString(), any(Object[].class));
    }
}

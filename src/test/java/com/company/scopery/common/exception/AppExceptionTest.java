package com.company.scopery.common.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AppExceptionTest {

    private static final ErrorCatalog SAMPLE_CATALOG = new ErrorCatalog() {
        @Override public String code() { return "SAMPLE_ERROR"; }
        @Override public String defaultMessage() { return "Default sample message"; }
        @Override public HttpStatus httpStatus() { return HttpStatus.NOT_FOUND; }
    };

    @Test
    void constructor_catalogOnly_usesDefaultMessage() {
        AppException ex = new AppException(SAMPLE_CATALOG);

        assertThat(ex.getMessage()).isEqualTo("Default sample message");
        assertThat(ex.getErrorCode()).isEqualTo("SAMPLE_ERROR");
        assertThat(ex.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(ex.getDetails()).isEmpty();
    }

    @Test
    void constructor_withCustomMessage_usesCustomMessage() {
        AppException ex = new AppException(SAMPLE_CATALOG, "Custom message");

        assertThat(ex.getMessage()).isEqualTo("Custom message");
        assertThat(ex.getErrorCode()).isEqualTo("SAMPLE_ERROR");
    }

    @Test
    void constructor_withNullMessage_fallsBackToDefaultMessage() {
        AppException ex = new AppException(SAMPLE_CATALOG, (String) null);

        assertThat(ex.getMessage()).isEqualTo("Default sample message");
    }

    @Test
    void constructor_withDetails_exposesDetails() {
        Map<String, Object> details = Map.of("id", "abc-123", "field", "code");
        AppException ex = new AppException(SAMPLE_CATALOG, "Error", details);

        assertThat(ex.getDetails()).containsEntry("id", "abc-123");
        assertThat(ex.getDetails()).containsEntry("field", "code");
    }

    @Test
    void constructor_withCause_wrapsThrowable() {
        Throwable cause = new RuntimeException("root cause");
        AppException ex = new AppException(SAMPLE_CATALOG, cause);

        assertThat(ex.getMessage()).isEqualTo("Default sample message");
        assertThat(ex.getCause()).isSameAs(cause);
    }

    @Test
    void getHttpStatus_returnsStatusFromCatalog() {
        ErrorCatalog conflictCatalog = new ErrorCatalog() {
            @Override public String code() { return "CONFLICT_ERROR"; }
            @Override public String defaultMessage() { return "Conflict"; }
            @Override public HttpStatus httpStatus() { return HttpStatus.CONFLICT; }
        };

        AppException ex = new AppException(conflictCatalog);

        assertThat(ex.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void details_areImmutable() {
        Map<String, Object> details = Map.of("key", "value");
        AppException ex = new AppException(SAMPLE_CATALOG, "msg", details);

        assertThat(ex.getDetails()).isUnmodifiable();
    }
}

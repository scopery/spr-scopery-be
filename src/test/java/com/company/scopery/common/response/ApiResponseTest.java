package com.company.scopery.common.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    @Test
    void success_setsSuccessTrueAndData() {
        ApiResponse<String> response = ApiResponse.success("hello");

        assertThat(response.success()).isTrue();
        assertThat(response.data()).isEqualTo("hello");
        assertThat(response.timestamp()).isNotBlank();
    }

    @Test
    void success_withNullData_stillValid() {
        ApiResponse<Void> response = ApiResponse.success(null);

        assertThat(response.success()).isTrue();
        assertThat(response.data()).isNull();
    }
}

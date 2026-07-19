package com.company.scopery.common.constant;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiPathsTest {

    @Test
    void basePath_isApi() {
        assertThat(ApiPaths.BASE_PATH).isEqualTo("/api");
    }

    @Test
    void iamAuthPath_isApiIamAuth() {
        assertThat(ApiPaths.IAM_AUTH).isEqualTo("/api/iam/auth");
    }

    @Test
    void healthPath_isApiHealth() {
        assertThat(ApiPaths.HEALTH).isEqualTo("/api/health");
    }

    @Test
    void noPublicConstantContainsApiV1() {
        assertThat(ApiPaths.BASE_PATH).doesNotContain("/api/v1");
        assertThat(ApiPaths.IAM_AUTH).doesNotContain("/api/v1");
        assertThat(ApiPaths.HEALTH).doesNotContain("/api/v1");
        assertThat(ApiPaths.IAM_USERS).doesNotContain("/api/v1");
    }

    @Test
    void noPublicConstantContainsApiV2() {
        assertThat(ApiPaths.BASE_PATH).doesNotContain("/api/v2");
        assertThat(ApiPaths.IAM_AUTH).doesNotContain("/api/v2");
        assertThat(ApiPaths.HEALTH).doesNotContain("/api/v2");
        assertThat(ApiPaths.IAM_USERS).doesNotContain("/api/v2");
    }
}

package com.company.scopery.common.pagination;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PageResponseTest {

    @Test
    void from_convertsSpringPageCorrectly() {
        List<String> content = List.of("a", "b", "c");
        Page<String> springPage = new PageImpl<>(content, PageRequest.of(0, 10), 25);

        PageResponse<String> response = PageResponse.from(springPage);

        assertThat(response.items()).containsExactly("a", "b", "c");
        assertThat(response.page()).isEqualTo(0);
        assertThat(response.size()).isEqualTo(10);
        assertThat(response.totalElements()).isEqualTo(25);
        assertThat(response.totalPages()).isEqualTo(3);
        assertThat(response.first()).isTrue();
        assertThat(response.last()).isFalse();
    }
}

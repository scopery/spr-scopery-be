package com.company.scopery.modules.reporting.shared.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CsvReportSerializerTest {

    private final CsvReportSerializer serializer = new CsvReportSerializer(new ObjectMapper());

    @Test
    void flattensNestedJsonIntoKeyValueRows() {
        String csv = serializer.toCsv("""
                {"totalTasks":10,"overdueTasks":3,"nested":{"blocked":1}}
                """);

        assertThat(csv).contains("key,value");
        assertThat(csv).contains("totalTasks,10");
        assertThat(csv).contains("overdueTasks,3");
        assertThat(csv).contains("nested.blocked,1");
        assertThat(csv).doesNotContain("report,{");
    }

    @Test
    void escapesCommasAndQuotes() {
        String csv = serializer.toCsv("{\"note\":\"hello, \\\"world\\\"\"}");
        assertThat(csv).contains("\"hello, \"\"world\"\"\"");
    }

    @Test
    void emptyJsonProducesHeaderOnly() {
        assertThat(serializer.toCsv("{}")).isEqualTo("key,value\n");
    }
}

package com.company.scopery;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Integration test — requires running PostgreSQL. Use ProviderCodeTest, ProviderTest, ProviderApplicationServiceTest for unit tests.")
class ScoperyApplicationTests {

    @Test
    void contextLoads() {
    }
}
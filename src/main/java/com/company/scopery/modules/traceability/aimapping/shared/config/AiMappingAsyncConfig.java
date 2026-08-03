package com.company.scopery.modules.traceability.aimapping.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AiMappingAsyncConfig {

    @Bean(name = "aiMappingTaskExecutor")
    public TaskExecutor aiMappingTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("ai-mapping-");
        executor.initialize();
        return executor;
    }
}

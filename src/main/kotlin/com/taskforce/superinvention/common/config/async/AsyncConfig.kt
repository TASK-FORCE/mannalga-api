package com.taskforce.superinvention.common.config.async

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@Configuration
@EnableAsync
class AsyncConfig {

    companion object {
        const val WEBP_CONVERSION = "webpConversion"
    }

    @Bean(WEBP_CONVERSION)
    fun webpConversionAsyncTaskExecutor(): Executor {

        val threadPoolTaskExecutor = ThreadPoolTaskExecutor()
        threadPoolTaskExecutor.corePoolSize = 10
        threadPoolTaskExecutor.maxPoolSize  = 30
        threadPoolTaskExecutor.setThreadNamePrefix("super-invention-webp-conversion")

        return threadPoolTaskExecutor
    }
}

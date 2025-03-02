package de.dkb.api.shortify.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.atomic.AtomicLong

@Configuration
class AppConfig {

    @Bean
    fun idGenerator(): AtomicLong {
        return AtomicLong(0)
    }
}
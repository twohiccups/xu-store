package com.xu_store.uniform.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties(prefix = "app.cors")
class CorsProps {
    var allowedOrigins: List<String> = listOf()
}

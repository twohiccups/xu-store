package com.xu_store.uniform.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "jwt")

class JwtConfig {
    lateinit var secret: String
    var expirationPeriod: Long = 1000 * 60 * 60 * 24

}

package com.xu_store.uniform.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Configuration
class CorsConfig(private val corsProperties: CorsProps) {

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        println(corsProperties.allowedOrigins)
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = corsProperties.allowedOrigins
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true
        configuration.allowPrivateNetwork = true
        configuration.exposedHeaders = listOf("Set-Cookie")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

}

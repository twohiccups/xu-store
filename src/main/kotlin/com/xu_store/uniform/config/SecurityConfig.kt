package com.xu_store.uniform.config

import com.xu_store.uniform.repository.UserDetailsServiceImpl
import com.xu_store.uniform.security.JwtAuthFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableMethodSecurity
class SecurityConfig(
    private val userDetailsServiceImpl: UserDetailsServiceImpl,
    private val jwtAuthFilter: JwtAuthFilter
) {

    /**
     * 1) Provide a PasswordEncoder Bean (BCrypt recommended).
     *    Used for hashing and verifying user passwords.
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    /**
     * 2) Provide a DaoAuthenticationProvider that uses
     *    our custom user details service and password encoder.
     */
    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(userDetailsServiceImpl)
        authProvider.setPasswordEncoder(passwordEncoder())
        return authProvider
    }

    /**
     * 3) Create an AuthenticationManager Bean by retrieving it from
     *    the Spring-provided AuthenticationConfiguration.
     *    This is the bean your AuthController needs to autowire.
     */
    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }

    /**
     * 4) Define the SecurityFilterChain to:
     *    - Disable CSRF (common with JWT)
     *    - Permit '/api/v1/login' and '/api/v1/register'
     *    - Require authentication for all other endpoints
     *    - Use stateless sessions
     *    - Register your DaoAuthenticationProvider
     *    - Add your JwtAuthFilter before UsernamePasswordAuthenticationFilter
     */
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers(HttpMethod.POST, "/api/v1/login", "/api/v1/register").permitAll()
                it.anyRequest().authenticated()
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}

package com.perflog.config.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.perflog.config.security.jwt.JwtAuthFilter
import com.perflog.config.security.jwt.JwtUtil
import com.perflog.config.security.jwt.LoginFilter
import com.perflog.domain.member.repository.MemberRepository
import com.perflog.domain.member.repository.RefreshTokenRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = false, securedEnabled = true)
class SecurityConfig(
    private val authenticationConfiguration: AuthenticationConfiguration,
    private val jwtUtil: JwtUtil,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val memberRepository: MemberRepository,
    private val objectMapper: ObjectMapper
) {

    @Bean
    fun authenticationManager(): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { csrf -> csrf.disable() }
            .formLogin { formLogin -> formLogin.disable() }
            .httpBasic { httpBasic -> httpBasic.disable() }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers(
                    "/", "/api/member/login", "/api/member/refresh"
                ).permitAll()
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/perfumes",
                        "/api/perfumes/*",
                        "/api/reviews/perfume/**"
                    ).permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/member").permitAll()

                    .requestMatchers(HttpMethod.POST, "/api/reviews", "/api/reviews/*")
                    .hasRole("USER")
                    .requestMatchers(HttpMethod.PUT, "/api/reviews/*").hasRole("USER")

                    .requestMatchers(HttpMethod.GET, "/api/member")
                    .hasAnyRole("USER", "ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/member").hasAnyRole("USER", "ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/reviews/*")
                    .hasAnyRole("USER", "ADMIN")
                    .requestMatchers("/api/member/logout").hasAnyRole("USER", "ADMIN")

                    .requestMatchers(HttpMethod.POST, "/api/perfumes").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/perfumes/*").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/perfumes/*").hasRole("ADMIN")
                    .anyRequest().authenticated()
            }
            .cors { cors ->
                cors.configurationSource {
                    CorsConfiguration().apply {
                        allowedOrigins = listOf("http://localhost:3000")
                        allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        allowCredentials = true
                        addExposedHeader("Set-Cookie")
                        addAllowedHeader("*")
                    }
                }
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .addFilterAt(
                LoginFilter(
                    authenticationManager(),
                    jwtUtil,
                    refreshTokenRepository,
                    memberRepository,
                    objectMapper
                ),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .addFilterBefore(
                JwtAuthFilter(jwtUtil, memberRepository),
                UsernamePasswordAuthenticationFilter::class.java
            )

        return http.build()
    }
}
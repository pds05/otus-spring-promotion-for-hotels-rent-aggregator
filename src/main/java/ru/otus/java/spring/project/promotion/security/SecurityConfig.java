package ru.otus.java.spring.project.promotion.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final WebAccessDeniedHandler webAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/login").permitAll()
                        .requestMatchers(HttpMethod.GET,"/promo_campaign").hasRole("USER")
                        .requestMatchers(HttpMethod.GET,"/api/v1/promo_campaign").hasRole("USER")

                        .requestMatchers(HttpMethod.GET,"/promo_campaign/**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.GET,"/api/v1/promo_campaign/**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.POST, "/promo_campaign").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/promo_campaign/**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/promo_campaign/**").hasRole("MANAGER")

                        .requestMatchers("/api/v1/campaign_hotel_parameter**").hasRole("MANAGER")
                        .requestMatchers("/api/v1/top_data**").hasRole("MANAGER")

                        .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .exceptionHandling(h -> h.accessDeniedHandler(webAccessDeniedHandler));
        return http.build();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
                .role("MANAGER").implies("USER")
                .build();
    }

    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy);
        return expressionHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

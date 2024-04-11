/*
 * Copyright (C) 2024 IUT Laval - Le Mans Université.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package explorateurIUT.configuration;

import explorateurIUT.security.authentication.LocalAdminDetailsService;
import explorateurIUT.security.csrf.CsrfCookieFilter;
import explorateurIUT.security.csrf.SpaCsrfTokenRequestHandler;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 *
 * @author Remi Venant
 */
@Configuration
@EnableWebSecurity
public class SecurityEndpointsConfiguration {

    private static final Log LOG = LogFactory.getLog(SecurityEndpointsConfiguration.class);

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(
            @Value("${app.security.admin-username}") String username,
            @Value("${app.security.admin-password}") String clearPassword,
            PasswordEncoder passwordEncoder) {
        final String encodedPassword = passwordEncoder.encode(clearPassword);
        return new LocalAdminDetailsService(username, encodedPassword);
    }

    @Bean
    public DaoAuthenticationProvider localAdminAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder encoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder);
        return authProvider;
    }

    @Bean(name = "basicCORSConfiguration")
    public CorsConfiguration basicCORSConfiguration() {
        final CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("OPTIONS", "HEAD", "GET"));
        configuration.setAllowedHeaders(Arrays.asList("content-type", "Accept", "Accept-Language", "X-Requested-With"));
        configuration.setAllowCredentials(Boolean.FALSE);
        configuration.setMaxAge(Duration.ofDays(7));
        return configuration;
    }

    @Bean(name = "devCORSConfiguration")
    @ConditionalOnProperty(name = "app.security.dev-cors", havingValue = "true")
    public CorsConfiguration devCORSConfiguration() {
        final CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:*", "http://127.0.0.1:*", "moz-extension://*"));
        configuration.setAllowedMethods(Arrays.asList("OPTIONS", "HEAD", "GET", "POST", "PUT", "PATCH", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("content-type", "Accept", "Accept-Language", "Authorization", "X-Requested-With"));
        configuration.setAllowCredentials(Boolean.TRUE);
        configuration.setMaxAge(Duration.ofHours(6));
        return configuration;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Qualifier("basicCORSConfiguration") CorsConfiguration basicCORSConfiguration,
            @Qualifier("devCORSConfiguration") Optional<CorsConfiguration> devCORSConfiguration) {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/v1/iut/**", basicCORSConfiguration);
        source.registerCorsConfiguration("/api/v1/referentiel/**", basicCORSConfiguration);
        if (devCORSConfiguration.isPresent()) {
            LOG.warn("CONFIGURE HTTP SECURITY WITH CORS - DEV");
            source.registerCorsConfiguration("/api/v1/admin/**", devCORSConfiguration.get());
        }
        return source;
    }

    @Bean
    public SecurityFilterChain MultiSecFilterChain(HttpSecurity http,
            CorsConfigurationSource corsConfigurationSource,
            @Value("${app.security.csrf:false}") boolean withCsrf) throws Exception {
        // Gestion de l'authentification : mot de passe dans en-tete
        http
                .httpBasic(basic -> {
                    basic.realmName("Local Administrative authentication");
                })
                .exceptionHandling(eh -> eh.accessDeniedHandler(new AccessDeniedHandlerImpl()));
        // Gestion des session : pas de session
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // Gestion du CORS
        http.cors(cors -> cors.configurationSource(corsConfigurationSource));
        // Protection CSRF pour une SPA (Double cookie, chiffré) si activé
        if (withCsrf) {
            LOG.info("ENFORCING CSRF");
            CookieCsrfTokenRepository csrfTokenRepo = new CookieCsrfTokenRepository();
            csrfTokenRepo.setCookieCustomizer((cookieBuilder) -> {
                cookieBuilder
                        .path("/")
                        .httpOnly(false)
                        .sameSite("lax").build();
            });
            http.csrf(csrf -> csrf
                    .csrfTokenRepository(csrfTokenRepo) // Token stocké dans un cookie
                    .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())) // Résolution mixte : en clair quand token transmis via en-tête ou param de requête, chiffré sinon
                    .addFilterAfter(new CsrfCookieFilter(), UsernamePasswordAuthenticationFilter.class); // pour la mise à jour du cookie au besoin
        } else {
            LOG.info("DISABLING CSRF");
            http.csrf(csrf -> csrf.disable());
        }

        // Par-feu applicatif
        http.authorizeHttpRequests(ahr -> {
            ahr
                    // CORS options
                    .requestMatchers(HttpMethod.OPTIONS).permitAll() // Autorise les requête OPTIONS à tous pour le CORS
                    // api/ configuration
                    .requestMatchers(HttpMethod.GET, "/api/v1/referentiel/**", "/api/v1/iut/**").permitAll() // Allow public access to data apis
                    .requestMatchers("/api/v1/admin/**").hasRole("ADMIN") // admin endpoints
                    .requestMatchers("/api/**").denyAll() // default policy for api
                    // errors
                    .requestMatchers(HttpMethod.GET, "/error").permitAll()
                    // static resources
                    .requestMatchers(HttpMethod.GET).permitAll()
                    // default policy
                    .anyRequest().denyAll(); // Tout autre requete interdit
        });

        return http.build();
    }
}

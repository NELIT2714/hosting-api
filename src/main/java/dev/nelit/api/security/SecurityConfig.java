package dev.nelit.api.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .authorizeExchange(auth -> auth
                .pathMatchers(
                    "/scalar/**",
                    "/v3/api-docs/**",
                    "/v3/api-docs.yaml"
                ).permitAll()

                .pathMatchers("/v1/webhooks/**").permitAll()
                .pathMatchers("/v1/auth/**").permitAll()

                .pathMatchers(HttpMethod.POST, "/v1/users").permitAll()
                .pathMatchers(HttpMethod.PATCH, "/v1/users/change-password").authenticated()

                // Plans
                .pathMatchers(HttpMethod.GET, "/v1/plans").authenticated()
                .pathMatchers(HttpMethod.POST, "/v1/plans").hasAuthority("PERMISSION_PLAN_CREATE")
                .pathMatchers(HttpMethod.PATCH, "/v1/plans/**").hasAuthority("PERMISSION_PLAN_UPDATE")
                .pathMatchers(HttpMethod.DELETE, "/v1/plans/**").hasAuthority("PERMISSION_PLAN_DELETE")

                // Nodes
                .pathMatchers(HttpMethod.GET, "/v1/nodes/locations").permitAll()
                .pathMatchers(HttpMethod.POST, "/v1/nodes").hasAuthority("PERMISSION_NODE_CREATE")
                .pathMatchers(HttpMethod.PATCH, "/v1/nodes/**").hasAuthority("PERMISSION_NODE_UPDATE")
                .pathMatchers(HttpMethod.DELETE, "/v1/nodes/**").hasAuthority("PERMISSION_NODE_DELETE")

                // IP pool
                .pathMatchers(HttpMethod.POST, "/v1/ip-pool").hasAuthority("PERMISSION_IP_CREATE")
                .pathMatchers(HttpMethod.DELETE, "/v1/ip-pool/**").hasAuthority("PERMISSION_IP_DELETE")

                // Vm
                .pathMatchers("/v1/vms").authenticated()
                .pathMatchers("/v1/vms/**").authenticated()

                // Checkout
                .pathMatchers("/v1/checkout").authenticated()

                // OS images
                .pathMatchers(HttpMethod.GET, "/v1/os-images").authenticated()
                .pathMatchers(HttpMethod.POST, "/v1/os-images").hasAuthority("PERMISSION_OS_IMAGE_CREATE")
                .pathMatchers(HttpMethod.DELETE, "/v1/os-images/**").hasAuthority("PERMISSION_OS_IMAGE_DELETE")

                .pathMatchers(HttpMethod.POST,   "/v1/admins").permitAll()
                .pathMatchers(HttpMethod.PATCH,  "/v1/admins/**").permitAll()
                .pathMatchers(HttpMethod.DELETE, "/v1/admins/**").permitAll()

                .anyExchange().denyAll()
            )
            .addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

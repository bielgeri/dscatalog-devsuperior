package com.devsuperior.dscatalog.config;

import org.springframework.core.env.Environment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	
	private final Environment env;
	
	private static final String[] OPERATOR_OR_ADMIN = {"products/**", "/categories/**"};
	private static final String[] ADMIN = {"/users/**"};

    WebSecurityConfig(Environment env) {
        this.env = env;
    }

    @Bean 
    @Order(2)
    SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
    	
    	
    	
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> {
                if (env.acceptsProfiles(Profiles.of("test"))) {
                    auth.requestMatchers("/h2-console/**").permitAll();
                } 
                
                auth
                .requestMatchers(HttpMethod.GET, OPERATOR_OR_ADMIN).permitAll()
                .requestMatchers(OPERATOR_OR_ADMIN).hasAnyRole("OPERATOR", "ADMIN")
                .requestMatchers(ADMIN).hasRole("ADMIN")
                .anyRequest().authenticated();
            })
            
            .formLogin(Customizer.withDefaults())
            .oauth2ResourceServer(oauth2 ->
            oauth2.jwt(jwt ->
            jwt.jwtAuthenticationConverter(
                jwtAuthenticationConverter
            )
        )
    );
        
        if (env.acceptsProfiles(Profiles.of("test"))) {
            http.headers(headers ->
                headers.frameOptions(frameOptions ->
                    frameOptions.disable()));
        }
        return http.build();
    }
           
    
    @Bean AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    @Bean
    JwtAuthenticationConverter  jwtAuthenticationConverter() {
    	JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
    	
    	authoritiesConverter.setAuthoritiesClaimName("roles");
    	authoritiesConverter.setAuthorityPrefix("");
    	
    	JwtAuthenticationConverter jwtAutheticationConverter = new JwtAuthenticationConverter();
    	
    	jwtAutheticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
    	
    	return jwtAutheticationConverter;
    }
}

package com.devsuperior.dscatalog.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
public class AppConfig {
	
    private final String jwtSecret = "MY-JWT-SECRET";

	@Bean BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	 @Bean JwtDecoder jwtDecoder() {
	        SecretKey originalKey = new SecretKeySpec(
	                jwtSecret.getBytes(StandardCharsets.UTF_8), 
	                "HmacSHA256"
	        );
	        return NimbusJwtDecoder.withSecretKey(originalKey).build();
	    }

	 @Bean JwtEncoder jwtEncoder() {
	        OctetSequenceKey jwk = new OctetSequenceKey.Builder(jwtSecret.getBytes(StandardCharsets.UTF_8))
	                .build();
	        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
	        return new NimbusJwtEncoder(jwks);
	    }
	 
	 @Bean SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	        return http
	                .csrf(csrf -> csrf.disable())
	                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	                .authorizeHttpRequests(auth -> auth
	                        .anyRequest().authenticated()
	                )
	                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder())))
	                .build();
	    }

}

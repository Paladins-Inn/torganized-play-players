package de.paladinsinn.tp.dcis.players.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import de.kaiserpfalzedv.commons.spring.security.EnableKeycloakSecurityIntegration;
import de.kaiserpfalzedv.commons.spring.security.KeycloakGroupAuthorityMapper;
import de.kaiserpfalzedv.commons.spring.security.KeycloakLogoutHandler;
import lombok.RequiredArgsConstructor;


@Configuration
@EnableWebSecurity(debug = false)
@EnableKeycloakSecurityIntegration
@Order(2)
@RequiredArgsConstructor
public class WebSecurityConfiguration {
    @Value("${spring.security.oauth2.resourceserver.jwt.issuerUri}")
    private String jwtIssuerUri;


    @Bean
	public SecurityFilterChain userSecurityFilterChain(
        HttpSecurity http, 
        HandlerMappingIntrospector introspector, 
        AuthenticationSuccessHandler successHandler,
        KeycloakGroupAuthorityMapper authoritiesMapper,
        KeycloakLogoutHandler keycloakLogoutHandler
        ) throws Exception {
        MvcRequestMatcher.Builder matcher = new MvcRequestMatcher.Builder(introspector);

		http
            .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers(matcher.pattern("/login/**")).permitAll()
                    .requestMatchers(matcher.pattern("/oauth2/**")).permitAll()
                    .requestMatchers(matcher.pattern("/dcis/**")).authenticated()
                    .requestMatchers(matcher.pattern("/logout/**")).authenticated()
                    .anyRequest().permitAll()
            )
            .oauth2Client(Customizer.withDefaults())
            .oauth2Login(l -> l
                .userInfoEndpoint(u -> u.userAuthoritiesMapper(authoritiesMapper))
                .successHandler(successHandler)
                .tokenEndpoint(Customizer.withDefaults())
                .userInfoEndpoint(Customizer.withDefaults())
            )
            .oauth2ResourceServer(Customizer.withDefaults())
            .logout(l -> l
                .addLogoutHandler(keycloakLogoutHandler)
                .logoutSuccessUrl("/players/")
            )
            .cors(Customizer.withDefaults())
            .csrf(Customizer.withDefaults())
            
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
            .rememberMe(Customizer.withDefaults())
            ;

        return http.build();
	}

	@Bean
	public JwtDecoder jwtDecoder() {
		return JwtDecoders.fromOidcIssuerLocation(jwtIssuerUri);
	}

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
        handler.setDefaultTargetUrl("/dcis/");
        return handler;
    }
}

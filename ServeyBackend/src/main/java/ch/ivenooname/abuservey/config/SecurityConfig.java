package ch.ivenooname.abuservey.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

import ch.ivenooname.abuservey.admin.AdminKeyFilter;

/**
 * Oeffentlich: Frontend, /api/survey/**, Swagger.
 * Geschuetzt: /api/admin/** per Header "X-Admin-Key".
 * CSRF ist aus, weil der Admin-Zugang NICHT auf Cookies/Browser-Sessions beruht (kein "ambient credential").
 */
@Configuration
public class SecurityConfig {

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http, AppProperties props) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable)
				.formLogin(AbstractHttpConfigurer::disable)
				.logout(AbstractHttpConfigurer::disable)
				.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
				.authorizeHttpRequests(a -> a
						.requestMatchers("/api/admin/**").authenticated()
						.anyRequest().permitAll())
				.addFilterBefore(new AdminKeyFilter(props.adminKey()), AuthorizationFilter.class)
				.headers(Customizer.withDefaults());
		return http.build();
	}

	/** Leerer User-Store: verhindert das automatisch generierte Passwort im Log. */
	@Bean
	UserDetailsService userDetailsService() {
		return new InMemoryUserDetailsManager();
	}
}

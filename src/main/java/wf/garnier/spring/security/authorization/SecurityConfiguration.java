package wf.garnier.spring.security.authorization;

import java.util.List;
import wf.garnier.spring.security.authorization.user.DemoUser;
import wf.garnier.spring.security.authorization.user.DemoUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
class SecurityConfiguration {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.authorizeHttpRequests(auth -> {
			auth.requestMatchers("/").permitAll();
			auth.requestMatchers("/css/*").permitAll();
			auth.requestMatchers("/favicon.ico").permitAll();
			auth.requestMatchers("/favicon.svg").permitAll();
			auth.requestMatchers("/error").permitAll();
			auth.anyRequest().authenticated();
		}).formLogin(formLogin -> formLogin.defaultSuccessUrl("/private")).build();
	}

	@Bean
	UserDetailsService userDetailsService() {
		return new DemoUserDetailsService(
				new DemoUser("josh", "password", "josh@example.com", List.of("user", "admin")),
				new DemoUser("daniel", "password", "daniel@example.com", List.of("user")));
	}

}

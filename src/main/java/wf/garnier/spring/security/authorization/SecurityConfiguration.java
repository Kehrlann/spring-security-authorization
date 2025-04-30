package wf.garnier.spring.security.authorization;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
		return new UserDetailsService() {
			private Map<String, DemoUser> users = Map.of("josh",
					new DemoUser("josh", "password", "josh@example.com", List.of("user", "admin")), "daniel",
					new DemoUser("daniel", "password", "daniel@example.com", List.of("user")));

			@Override
			public DemoUser loadUserByUsername(String username) throws UsernameNotFoundException {
				var user = users.get(username);
				if (user == null) {
					throw new UsernameNotFoundException(username);
				}
				return user;
			}
		};
	}

}

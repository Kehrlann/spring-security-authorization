package wf.garnier.spring.security.authorization;

import java.util.List;
import java.util.function.Supplier;
import wf.garnier.spring.security.authorization.user.DemoUser;
import wf.garnier.spring.security.authorization.user.DemoUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagers;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AnnotationTemplateExpressionDefaults;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfiguration {

	@Bean
	@Order(1)
	SecurityFilterChain localhostFilterChain(HttpSecurity http) throws Exception {
		return http
				.securityMatcher("/localhost/**")
				.authorizeHttpRequests(auth -> {
					auth.anyRequest().access((_, reqCtx) -> new AuthorizationDecision(reqCtx.getRequest().getServerName().equals("localhost")));
				})
				.build();
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.authorizeHttpRequests(auth -> {
			auth.requestMatchers("/").permitAll();
			auth.requestMatchers("/css/*").permitAll();
			auth.requestMatchers("/favicon.ico").permitAll();
			auth.requestMatchers("/favicon.svg").permitAll();
			auth.requestMatchers("/error").permitAll();
			auth.requestMatchers("/admin").hasRole("admin");
			//@formatter:off
			auth.requestMatchers("/profile/{username}").access(
					AuthorizationManagers.anyOf(
						new AuthenticationNameMatchesVariable("username"),
						AuthorityAuthorizationManager.hasRole("admin")
					)
			);
			//@formatter:on
			auth.requestMatchers("/corp").access((authSupplier, reqCtx) -> {
				var authentication = authSupplier.get();
				if (!(authentication.getPrincipal() instanceof DemoUser u)) {
					return new AuthorizationDecision(false);
				}

				var isCorp = u.getEmail().domain().equals("corp.example.com")
						|| u.getEmail().domain().equals("example.com");
				return new AuthorizationDecision(isCorp);
			});
			auth.requestMatchers("/http-basic").access((authSupplier, reqCtx) -> {
				var actual = authSupplier.get();
				if (AuthenticationTypeAuthenticationDetailsSource.AuthenticationType.HTTP_BASIC
					.equals(actual.getDetails())) {
					return new AuthorizationDecision(true);
				}
				return new AuthorizationDecision(false);
			});
			auth.anyRequest().authenticated();
		}).formLogin(formLogin -> {
			formLogin.defaultSuccessUrl("/private");
			formLogin.authenticationDetailsSource(new AuthenticationTypeAuthenticationDetailsSource(
					AuthenticationTypeAuthenticationDetailsSource.AuthenticationType.FORM_LOGIN));
		}).httpBasic(httpBasic -> {
			httpBasic.authenticationDetailsSource(new AuthenticationTypeAuthenticationDetailsSource(
					AuthenticationTypeAuthenticationDetailsSource.AuthenticationType.HTTP_BASIC));
		}).build();
	}

	@Bean
	DemoUserDetailsService userDetailsService() {
		return new DemoUserDetailsService(
				new DemoUser("josh", "password", "josh@example.com", List.of("user", "admin")),
				new DemoUser("daniel", "password", "daniel@example.com", List.of("user")),
				new DemoUser("alice", "password", "alice@corp.example.com", List.of("user")),
				new DemoUser("bob", "password", "bob@ext.example.com", List.of("user")));
	}

	@Bean
	AnnotationTemplateExpressionDefaults annotationTemplateExpressionDefaults() {
		return new AnnotationTemplateExpressionDefaults();
	}

	private static class AuthenticationNameMatchesVariable
			implements AuthorizationManager<RequestAuthorizationContext> {

		private final String variableName;

		public AuthenticationNameMatchesVariable(String variableName) {
			this.variableName = variableName;
		}

		@Override
		public AuthorizationDecision check(Supplier<Authentication> authentication,
				RequestAuthorizationContext requestContext) {
			var value = requestContext.getVariables().get(variableName);
			var auth = authentication.get();
			if (auth.getName().equals(value)) {
				return new AuthorizationDecision(true);
			}

			return new AuthorizationDecision(false);
		}

	}

}

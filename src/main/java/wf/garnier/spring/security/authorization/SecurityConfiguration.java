package wf.garnier.spring.security.authorization;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import wf.garnier.spring.security.authorization.user.DemoUser;
import wf.garnier.spring.security.authorization.user.DemoUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
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
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfiguration {

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
							AuthorityAuthorizationManager.hasRole("admin"),
							new PathVariableAuthorizationManager("username")
					)
			);
			//@formatter:on
			auth.requestMatchers("/localhost").access(new LocalhostAuthorizationManager());
			auth.requestMatchers("/http-basic").access((authSupplier, requestContext) -> {
				var isHttpBasic = Optional.ofNullable(authSupplier.get())
					.map(Authentication::getDetails)
					.map(AuthenticationType.HTTP_BASIC::equals)
					.orElse(false);
				return new AuthorizationDecision(isHttpBasic);
			});
			auth.anyRequest().authenticated();
		}).formLogin(formLogin -> {
			formLogin.defaultSuccessUrl("/private");
			formLogin
				.authenticationDetailsSource(new LoginAwareAuthenticationDetailsSource(AuthenticationType.FORM_LOGIN));
		}).httpBasic(httpBasic -> {
			httpBasic
				.authenticationDetailsSource(new LoginAwareAuthenticationDetailsSource(AuthenticationType.HTTP_BASIC));
		})
			.exceptionHandling(exception -> exception.defaultAuthenticationEntryPointFor(
					// on /localhost, throw HTTP 403 instead of redirecting to login page
					new Http403ForbiddenEntryPoint(), new AntPathRequestMatcher("/localhost")))
			.build();
	}

	@Bean
	DemoUserDetailsService userDetailsService() {
		return new DemoUserDetailsService(
				new DemoUser("josh", "password", "josh@example.com", List.of("user", "admin")),
				new DemoUser("daniel", "password", "daniel@example.com", List.of("user")));
	}

	@Bean
	AnnotationTemplateExpressionDefaults templateExpressionDefaults() {
		return new AnnotationTemplateExpressionDefaults();
	}

	private static class LocalhostAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

		@Override
		public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier,
				RequestAuthorizationContext requestContext) {
			return new AuthorizationDecision("localhost".equals(requestContext.getRequest().getServerName()));
		}

	}

	private static class LoginAwareAuthenticationDetailsSource
			implements AuthenticationDetailsSource<HttpServletRequest, AuthenticationType> {

		private final AuthenticationType authenticationType;

		private LoginAwareAuthenticationDetailsSource(AuthenticationType authenticationType) {
			this.authenticationType = authenticationType;
		}

		@Override
		public AuthenticationType buildDetails(HttpServletRequest context) {
			return this.authenticationType;
		}

	}

	enum AuthenticationType {

		FORM_LOGIN, HTTP_BASIC

	}

	private static class PathVariableAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

		private final String pathVariable;

		public PathVariableAuthorizationManager(String pathVariable) {
			this.pathVariable = pathVariable;
		}

		@Override
		public AuthorizationDecision check(Supplier<Authentication> authSupplier,
				RequestAuthorizationContext requestContext) {
			if (authSupplier.get() == null) {
				return new AuthorizationDecision(false);
			}
			var pathValue = requestContext.getVariables().get(this.pathVariable);
			return new AuthorizationDecision(authSupplier.get().getName().equals(pathValue));
		}

	}

}

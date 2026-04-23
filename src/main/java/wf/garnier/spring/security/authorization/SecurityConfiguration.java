package wf.garnier.spring.security.authorization;

import java.time.Duration;
import java.util.List;

import wf.garnier.spring.security.authorization.ott.MailNotifier;
import wf.garnier.spring.security.authorization.user.DemoOidcUserService;
import wf.garnier.spring.security.authorization.user.DemoUser;
import wf.garnier.spring.security.authorization.user.DemoUserDetailsService;

import org.springframework.boot.web.server.autoconfigure.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.authorization.AllRequiredFactorsAuthorizationManager;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManagerFactories;
import org.springframework.security.authorization.AuthorizationManagers;
import org.springframework.security.authorization.RequiredFactor;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authorization.EnableMultiFactorAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.annotation.AnnotationTemplateExpressionDefaults;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestClient;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableMultiFactorAuthentication(authorities = {})
class SecurityConfiguration {

	@Bean
	@Order(1)
	SecurityFilterChain localhostFilterChain(HttpSecurity http) throws Exception {
		return http.securityMatcher("/localhost/**").authorizeHttpRequests(auth -> {
			auth.anyRequest()
				.access((_,
						reqCtx) -> new AuthorizationDecision(reqCtx.getRequest().getServerName().equals("localhost")));
		}).build();
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, Environment environment, MailNotifier mailNotifier,
			ServerProperties serverProperties) throws Exception {
		if (environment.matchesProfiles("sso")) {
			http.oauth2Login(Customizer.withDefaults());
		}

		var mfa = AuthorizationManagerFactories.multiFactor()
			.requireFactors(FactorGrantedAuthority.PASSWORD_AUTHORITY, FactorGrantedAuthority.OTT_AUTHORITY)
			.build();

		var recentPassword = AllRequiredFactorsAuthorizationManager.builder()
			.requireFactor(RequiredFactor.builder().passwordAuthority().validDuration(Duration.ofSeconds(30)).build())
			.build();

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
						new NameMatchesVariableAuthorizationManager("username"),
						AuthorityAuthorizationManager.hasRole("admin")
					)
			);
			//@formatter:on
			auth.requestMatchers("/corp").access((authSupplier, reqCtx) -> {
				var authentication = authSupplier.get();
				if (!(authentication.getPrincipal() instanceof DemoUser u)) {
					return new AuthorizationDecision(false);
				}

				var isCorp = u.getUserEmail().domain().equals("corp.example.com")
						|| u.getUserEmail().domain().equals("example.com");
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

			auth.requestMatchers("/admin-mfa").access(mfa.hasRole("admin"));
			auth.requestMatchers("/password").access(recentPassword);
			auth.anyRequest().authenticated();
		}).formLogin(formLogin -> {
			formLogin.defaultSuccessUrl("/private");
			formLogin.authenticationDetailsSource(new AuthenticationTypeAuthenticationDetailsSource(
					AuthenticationTypeAuthenticationDetailsSource.AuthenticationType.FORM_LOGIN));
		}).oneTimeTokenLogin(ott -> {
			ott.tokenGenerationSuccessHandler((request, response, oneTimeToken) -> {
				mailNotifier.notify("Log in to demo", "Use token: %s".formatted(oneTimeToken.getTokenValue()),
						"http://localhost:%s/login/ott?token=%s".formatted(serverProperties.getPort(),
								oneTimeToken.getTokenValue()));
				response.sendRedirect("/login/ott");
			});
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

	@Bean
	OpenFgaClient openFgaClient(RestClient.Builder builder) {
		return OpenFgaClient.create(builder);
	}

	@Bean
	OidcUserService oidcUserService() {
		return new DemoOidcUserService();
	}

}

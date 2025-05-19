package wf.garnier.spring.security.authorization;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationDetailsSource;

public class AuthenticationTypeAuthenticationDetailsSource implements
		AuthenticationDetailsSource<HttpServletRequest, AuthenticationTypeAuthenticationDetailsSource.AuthenticationType> {

	private final AuthenticationType type;

	public AuthenticationTypeAuthenticationDetailsSource(AuthenticationType type) {
		this.type = type;
	}

	@Override
	public AuthenticationType buildDetails(HttpServletRequest context) {
		return this.type;
	}

	public enum AuthenticationType {

		HTTP_BASIC, FORM_LOGIN

	}

}

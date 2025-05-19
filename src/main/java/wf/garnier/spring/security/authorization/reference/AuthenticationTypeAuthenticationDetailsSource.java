package wf.garnier.spring.security.authorization.reference;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationDetailsSource;

class AuthenticationTypeAuthenticationDetailsSource implements
		AuthenticationDetailsSource<HttpServletRequest, AuthenticationTypeAuthenticationDetailsSource.AuthenticationType> {

	private final AuthenticationType type;

	public AuthenticationTypeAuthenticationDetailsSource(AuthenticationType type) {
		this.type = type;
	}

	@Override
	public AuthenticationType buildDetails(HttpServletRequest context) {
		return this.type;
	}

	enum AuthenticationType {

		HTTP_BASIC, FORM_LOGIN

	}

}

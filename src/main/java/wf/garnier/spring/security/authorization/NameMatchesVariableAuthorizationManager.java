package wf.garnier.spring.security.authorization;

import java.util.function.Supplier;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

class NameMatchesVariableAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

	private final String variableName;

	public NameMatchesVariableAuthorizationManager(String variableName) {
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

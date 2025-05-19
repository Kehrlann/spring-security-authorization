package wf.garnier.spring.security.authorization;

import java.util.Arrays;
import wf.garnier.spring.security.authorization.user.DemoUser;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

	public boolean sameDomain(Authentication authentication, DemoUser otherUser) {
		if (!(authentication.getPrincipal() instanceof DemoUser authUser)) {
			return false;
		}

		return authUser.getEmail().domain().equals(otherUser.getEmail().domain());
	}

	public boolean hasAllowedDomain(Authentication authentication, String[] allowedDomains) {
		if (!(authentication.getPrincipal() instanceof DemoUser authUser)) {
			return false;
		}

		return Arrays.stream(allowedDomains).anyMatch(d -> d.equals(authUser.getEmail().domain()));
	}

}

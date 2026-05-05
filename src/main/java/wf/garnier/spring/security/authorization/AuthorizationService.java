package wf.garnier.spring.security.authorization;

import java.util.Arrays;
import java.util.Objects;

import wf.garnier.spring.security.authorization.user.DemoUser;
import wf.garnier.spring.security.authorization.user.UserEmail;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

	public boolean sameDomain(Object authenticatedPrincipal, DemoUser otherUser) {
        if (!(authenticatedPrincipal instanceof UserEmail authUser)) {
			return false;
		}

		return authUser.getUserEmail().domain().equals(otherUser.getUserEmail().domain());
	}

	public boolean hasAllowedDomain(Object authenticatedPrincipal, String[] allowedDomains) {
		if (!(authenticatedPrincipal instanceof UserEmail authUser)) {
			return false;
		}

		return Arrays.stream(allowedDomains).anyMatch(d -> d.equals(authUser.getUserEmail().domain()));
	}

}

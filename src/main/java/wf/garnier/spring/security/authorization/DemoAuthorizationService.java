package wf.garnier.spring.security.authorization;

import wf.garnier.spring.security.authorization.user.DemoUser;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
class DemoAuthorizationService {

	public boolean isCorporate(Authentication authentication) {
		if (authentication == null) {
			return false;
		}
		if (!(authentication.getPrincipal() instanceof DemoUser user)) {
			return false;
		}
		return user.getEmail().split("@")[1].equals("corp.example.com");
	}

}

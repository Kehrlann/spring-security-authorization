package wf.garnier.spring.security.authorization;

import wf.garnier.spring.security.authorization.user.DemoUser;
import wf.garnier.spring.security.authorization.user.DemoUserDetailsService;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
class DemoService {

	private final DemoUserDetailsService demoUserDetailsService;

	public DemoService(DemoUserDetailsService demoUserDetailsService) {
		this.demoUserDetailsService = demoUserDetailsService;
	}

	@PreAuthorize("hasRole('admin') or authentication.getName() == #username")
	public String profile(String username) {
		return "Hello " + username;
	}

	@PreAuthorize("@demoAuthorizationService.isCorporate(authentication)")
	public String corporate(String username) {
		return username + " is part of Corp.";
	}

	@EmailDomain(domains = { "corp.example.com", "ext.example.com" })
	public String emailAllowList(String username) {
		return username + " has a valid email address.";
	}

	@PostAuthorize("@demoAuthorizationService.hasSameDomain(authentication, returnObject)")
	public DemoUser emailByUsername(String username) {
		var user = demoUserDetailsService.loadUserByUsername(username);
		return user;
	}

}

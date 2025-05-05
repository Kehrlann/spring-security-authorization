package wf.garnier.spring.security.authorization;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
class DemoService {

	@PreAuthorize("hasRole('admin') or authentication.getName() == #username")
	public String profile(String username) {
		return "Hello " + username;
	}

}

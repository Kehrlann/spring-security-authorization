package wf.garnier.spring.security.authorization.user;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class DemoUserDetailsService implements UserDetailsService {

	private final Map<String, DemoUser> users;

	public DemoUserDetailsService(DemoUser... users) {
		this.users = Arrays.stream(users)
			.collect(Collectors.toUnmodifiableMap(DemoUser::getUsername, Function.identity()));
	}

	@Override
	public DemoUser loadUserByUsername(String username) throws UsernameNotFoundException {
		var user = users.get(username);
		if (user == null) {
			throw new UsernameNotFoundException(username);
		}
		return new DemoUser(user);
	}

	/**
	 * Expose a new method for demo purposes, so we can annotate it.
	 */
	public DemoUser findUser(String username) {
		return loadUserByUsername(username);
	}

}

package wf.garnier.spring.security.authorization.user;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

public class DemoUserDetailsService implements UserDetailsService {

	private final Map<String, DemoUser> users;

	private final PasswordEncoder passwordEncoder;

	public DemoUserDetailsService(DemoUser... users) {
		this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

		this.users = Arrays.stream(users).collect(Collectors.toMap(DemoUser::getUsername, Function.identity()));
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
		try {
			return loadUserByUsername(username);
		}
		catch (UsernameNotFoundException e) {
			throw new RuntimeException("Username not found: " + username);
		}
	}

	public void updatePassword(String username, String newPassword) {
		var existingUser = loadUserByUsername(username);
		var updated = new DemoUser(existingUser.getUsername(), passwordEncoder.encode(newPassword),
				existingUser.getUserEmail().toString(), existingUser.getRoles());
		users.put(username, updated);
	}

}

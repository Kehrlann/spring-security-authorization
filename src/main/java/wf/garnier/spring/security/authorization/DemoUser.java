package wf.garnier.spring.security.authorization;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class DemoUser implements UserDetails, CredentialsContainer {

	private final String username;

	private String password;

	private final Collection<? extends GrantedAuthority> authorities;

	private final String email;

	public DemoUser(String username, String password, String email, List<String> roles) {
		this.email = email;
		this.password = password;
		this.username = username;
		this.authorities = roles.stream()
			.map(r -> r.startsWith("ROLE_") ? new SimpleGrantedAuthority(r) : new SimpleGrantedAuthority("ROLE_" + r))
			.toList();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getPassword() {
		return "{noop}" + this.password;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public void eraseCredentials() {
		this.password = null;
	}

	public String getEmail() {
		return this.email;
	}

}

package wf.garnier.spring.security.authorization.user;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class DemoUser implements UserDetails, CredentialsContainer {

	private final String username;

	private String password;

	private final Collection<? extends GrantedAuthority> authorities;

	private final Email email;

	public DemoUser(String username, String password, String email, List<String> roles) {
		this.email = new Email(email);
		this.password = password;
		this.username = username;
		this.authorities = roles.stream()
			.map(r -> r.startsWith("ROLE_") ? new SimpleGrantedAuthority(r) : new SimpleGrantedAuthority("ROLE_" + r))
			.toList();
	}

	DemoUser(DemoUser other) {
		this.username = other.getUsername();
		this.password = other.getPassword().replace("{noop}", "");
		this.email = other.getEmail();
		this.authorities = other.getAuthorities();
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

	public Email getEmail() {
		return this.email;
	}

	public record Email(String address, String domain) {
		public Email(String email) {
			this(email.split("@")[0], email.split("@")[1]);
		}

		@Override
		public String toString() {
			return "%s@%s".formatted(address, domain);
		}
	}

	public List<String> getRoles() {
		return this.authorities.stream()
			.map(GrantedAuthority::getAuthority)
			.map(r -> r.replace("ROLE_", ""))
			.collect(Collectors.toList());
	}

}

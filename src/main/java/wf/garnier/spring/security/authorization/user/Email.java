package wf.garnier.spring.security.authorization.user;

import java.io.Serializable;

public record Email(String address, String domain) implements Serializable {
	public Email(String email) {
		this(email.split("@")[0], email.split("@")[1]);
	}

	@Override
	public String toString() {
		return "%s@%s".formatted(address, domain);
	}
}

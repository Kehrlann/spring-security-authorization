package wf.garnier.spring.security.authorization.user;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

public class DemoOidcUser extends DefaultOidcUser implements UserEmail {

	public DemoOidcUser(Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken, OidcUserInfo userInfo) {
		super(authorities, idToken, userInfo);
	}

	@Override
	public Email getUserEmail() {
		return new Email(getEmail());
	}

}

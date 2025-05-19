package wf.garnier.spring.security.authorization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@PreAuthorize("@authorizationService.hasAllowedDomain(authentication, '{domains}')")
public @interface HasDomain {

	/**
	 * Allow-list of domains.
	 */
	String[] domains();

}

package wf.garnier.spring.security.authorization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@PreAuthorize("@demoAuthorizationService.hasDomain(authentication, '{domains}')")
public @interface EmailDomain {

	String[] domains();

}

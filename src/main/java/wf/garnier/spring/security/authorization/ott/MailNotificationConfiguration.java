package wf.garnier.spring.security.authorization.ott;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class MailNotificationConfiguration {

	@Bean
	public MailNotifier mailNotifier() {
		if (MacOsMailNotifier.isSupported()) {
			return new MacOsMailNotifier();
		}
		else {
			return new ConsoleMailNotifier();
		}
	}

}

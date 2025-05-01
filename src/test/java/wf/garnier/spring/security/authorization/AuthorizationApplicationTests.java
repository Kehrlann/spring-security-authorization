package wf.garnier.spring.security.authorization;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorizationApplicationTests {

	@Autowired
	private MockMvcTester mvc;

	@Test
	void login() {
		var response = mvc.post()
			.uri("/login")
			.param("username", "daniel")
			.param("password", "password")
			.with(csrf())
			.exchange();

		assertThat(response).hasStatus(HttpStatus.FOUND).hasRedirectedUrl("/private");
	}

	@Test
	void loginFails() {
		var response = mvc.post()
			.uri("/login")
			.param("username", "daniel")
			.param("password", "wrong-password")
			.with(csrf())
			.exchange();

		assertThat(response).hasStatus(HttpStatus.FOUND).hasRedirectedUrl("/login?error");
	}

}

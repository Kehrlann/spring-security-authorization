package wf.garnier.spring.security.authorization;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorizationApplicationTests {

	@Autowired
	private MockMvcTester mvc;

	@Nested
	class Login {

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

	@Nested
	class Admin {

		@Test
		@WithUserDetails(value = "josh") // fetches the real "josh" user, who is admin
		void adminPage() {
			var response = mvc.get().uri("/admin").exchange();

			assertThat(response).hasStatus(HttpStatus.OK);
		}

		@Test
		@WithMockUser(value = "is-not-admin", roles = { "user" })
		void adminPageForbidden() {
			var response = mvc.get().uri("/admin").exchange();

			assertThat(response).hasStatus(HttpStatus.FORBIDDEN);
		}

	}

	@Nested
	class Profile {

		@Test
		@WithUserDetails(value = "daniel")
		void profilePage() {
			var response = mvc.get().uri("/profile/daniel").exchange();

			assertThat(response).hasStatus(HttpStatus.OK).bodyText().contains("daniel@example.com");
		}

		@Test
		@WithUserDetails(value = "daniel")
		void profilePageForbidden() {
			var response = mvc.get().uri("/profile/felix").exchange();

			assertThat(response).hasStatus(HttpStatus.FORBIDDEN);
		}

		@Test
		@WithUserDetails(value = "josh")
		void profilePageAdmin() {
			var response = mvc.get().uri("/profile/daniel").exchange();

			assertThat(response).hasStatus(HttpStatus.OK).bodyText().contains("daniel@example.com");
		}

	}

	@Nested
	class Localhost {

		@Test
		void localhost() {
			var response = mvc.get().uri("/localhost").exchange();

			assertThat(response).hasStatus(HttpStatus.OK).bodyText().contains("localhost");
		}

		@Test
		void localhostForbidden() {
			var response = mvc.get().uri("/localhost").with(request -> {
				request.setServerName("127.0.0.1");
				return request;
			}).exchange();

			assertThat(response).hasStatus(HttpStatus.FORBIDDEN);
		}

	}

	@Nested
	class HttpBasic {

		@Test
		void httpBasic() {
			var response = mvc.get()
				.uri("/http-basic")
				.with(SecurityMockMvcRequestPostProcessors.httpBasic("daniel", "password"))
				.exchange();

			assertThat(response).hasStatus(HttpStatus.OK).bodyText().contains("HTTP Basic");
		}

		@Test
		@WithMockUser("test-user")
		void httpBasicForbidden() {
			var response = mvc.get().uri("/http-basic").exchange();

			assertThat(response).hasStatus(HttpStatus.FORBIDDEN);
		}

	}

}

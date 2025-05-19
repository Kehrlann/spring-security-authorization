package wf.garnier.spring.security.authorization;

import java.util.Collections;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.security.authorization.user.DemoUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
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
	class PrivatePage {

		@Test
		@WithUserDetails("daniel") // use real DemoUser "daniel"
		void privatePage() {
			var response = mvc.get().uri("/private").exchange();

			assertThat(response).hasStatus(HttpStatus.OK).bodyText().doesNotContain("Shipment information");
		}

		@Test
		@WithUserDetails("josh")
		void privatePageAdmin() {
			var response = mvc.get().uri("/private").exchange();

			assertThat(response).hasStatus(HttpStatus.OK).bodyText().contains("Shipment information");
		}

		@Test
		void privatePageAnonymous() {
			var response = mvc.get().uri("/private").exchange();

			assertThat(response).hasStatus(HttpStatus.FOUND).redirectedUrl().endsWith("/login");
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
		@WithMockUser(value = "daniel")
		void profilePage() {
			var response = mvc.get().uri("/profile/daniel").exchange();

			assertThat(response).hasStatus(HttpStatus.OK).bodyText().contains("daniel@example.com");
		}

		@Test
		@WithMockUser(value = "test-user")
		void profilePageForbidden() {
			var response = mvc.get().uri("/profile/felix").exchange();

			assertThat(response).hasStatus(HttpStatus.FORBIDDEN);
		}

		@Test
		@WithMockUser(value = "test-user", roles = "admin")
		void profilePageAdmin() {
			var response = mvc.get().uri("/profile/daniel").exchange();

			assertThat(response).hasStatus(HttpStatus.OK).bodyText().contains("daniel@example.com");
		}

	}

	@Nested
	class Corporate {

		@Test
		void corp() {
			var response = mvc.get().uri("/corp").with(user("alice", "corp.example.com")).exchange();

			assertThat(response).hasStatus(HttpStatus.OK).bodyText().contains("alice is part of Corp.");
		}

		@Test
		void corpForbidden() {
			var response = mvc.get().uri("/corp").with(user("bob", "ext.example.com")).exchange();

			assertThat(response).hasStatus(HttpStatus.FORBIDDEN);
		}

		@Test
		void corpRoot() {
			var response = mvc.get().uri("/corp").with(user("carol", "example.com")).exchange();

			assertThat(response).hasStatus(HttpStatus.OK).bodyText().contains("carol is part of Corp.");
		}

		@Test
		@WithMockUser("bob")
		void corpRawUser() {
			var response = mvc.get().uri("/corp").exchange();

			assertThat(response).hasStatus(HttpStatus.FORBIDDEN);
		}

		@Test
		void corpAnonymous() {
			var response = mvc.get().uri("/corp").exchange();

			assertThat(response).hasStatus(HttpStatus.FOUND).redirectedUrl().endsWith("/login");
		}

	}

	private static RequestPostProcessor user(String username, String emailDomain) {
		return SecurityMockMvcRequestPostProcessors
			.user(new DemoUser(username, null, username + "@" + emailDomain, Collections.emptyList()));
	}

}

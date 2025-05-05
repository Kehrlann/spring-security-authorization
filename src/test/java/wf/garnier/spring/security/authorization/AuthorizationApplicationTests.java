package wf.garnier.spring.security.authorization;

import java.util.Collections;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.security.authorization.user.DemoUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

	@Nested
	class Method {

		@Nested
		class Profile {

			@Test
			@WithMockUser("test-user")
			void methodProfile() {
				var response = mvc.get().uri("/method/profile/test-user").exchange();

				assertThat(response).hasStatus(HttpStatus.OK).bodyText().contains("Hello test-user");
			}

			@Test
			@WithMockUser(username = "test-admin", roles = { "user", "admin" })
			void methodProfileAdmin() {
				var response = mvc.get().uri("/method/profile/test-user").exchange();

				assertThat(response).hasStatus(HttpStatus.OK).bodyText().contains("Hello test-user");
			}

			@Test
			@WithMockUser("other-user")
			void methodProfileForbidden() {
				var response = mvc.get().uri("/method/profile/test-user").exchange();

				assertThat(response).hasStatus(HttpStatus.FORBIDDEN);
			}

			@Test
			void methodProfileAnonymous() {
				var response = mvc.get().uri("/method/profile/test-user").accept(MediaType.TEXT_HTML).exchange();

				assertThat(response).hasStatus(HttpStatus.FOUND).redirectedUrl().endsWith("/login");
			}

		}

		@Nested
		class Corporate {

			@Test
			void methodCorporate() {
				var response = mvc.get().uri("/method/corporate").with(user("alice", "corp.example.com")).exchange();

				assertThat(response).hasStatus(HttpStatus.OK).bodyText().contains("alice is part of Corp.");
			}

			@Test
			void methodCorporateForbidden() {
				var response = mvc.get().uri("/method/corporate").with(user("bob", "example.com")).exchange();

				assertThat(response).hasStatus(HttpStatus.FORBIDDEN);
			}

			@Test
			@WithMockUser("bob")
			void methodCorporateRawUser() {
				var response = mvc.get().uri("/method/corporate").exchange();

				assertThat(response).hasStatus(HttpStatus.FORBIDDEN);
			}

			@Test
			void methodCorporateAnonymous() {
				var response = mvc.get().uri("/method/corporate").accept(MediaType.TEXT_HTML).exchange();

				assertThat(response).hasStatus(HttpStatus.FOUND).redirectedUrl().endsWith("/login");
			}

		}

		@Nested
		class AllowedDomains {

			@Test
			void methodAllowedDomain() {
				assertThat(mvc.get().uri("/method/allowed-domain").with(user("alice", "corp.example.com")).exchange())
					.hasStatus(HttpStatus.OK)
					.bodyText()
					.contains("alice has a valid email address.");
				assertThat(mvc.get().uri("/method/allowed-domain").with(user("carol", "ext.example.com")).exchange())
					.hasStatus(HttpStatus.OK)
					.bodyText()
					.contains("carol has a valid email address.");
			}

			@Test
			void methodAllowedDomainForbidden() {
				var response = mvc.get().uri("/method/allowed-domain").with(user("bob", "example.com")).exchange();

				assertThat(response).hasStatus(HttpStatus.FORBIDDEN);
			}

			@Test
			@WithMockUser("bob")
			void methodAllowedDomainRawUser() {
				var response = mvc.get().uri("/method/allowed-domain").exchange();

				assertThat(response).hasStatus(HttpStatus.FORBIDDEN);
			}

			@Test
			void methodAllowedDomainAnonymous() {
				var response = mvc.get().uri("/method/allowed-domain").accept(MediaType.TEXT_HTML).exchange();

				assertThat(response).hasStatus(HttpStatus.FOUND).redirectedUrl().endsWith("/login");
			}

		}

	}

	private static RequestPostProcessor user(String username, String emailDomain) {
		return SecurityMockMvcRequestPostProcessors
			.user(new DemoUser(username, null, username + "@" + emailDomain, Collections.emptyList()));
	}

}

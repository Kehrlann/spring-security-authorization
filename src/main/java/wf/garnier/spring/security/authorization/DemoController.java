package wf.garnier.spring.security.authorization;

import wf.garnier.spring.security.authorization.user.DemoUser;
import wf.garnier.spring.security.authorization.user.DemoUserDetailsService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
class DemoController {

	private final DemoUserDetailsService userDetailsService;

	private final DemoService demoService;

	public DemoController(DemoUserDetailsService userDetailsService, DemoService demoService) {
		this.userDetailsService = userDetailsService;
		this.demoService = demoService;
	}

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/private")
	public String privatePage(@AuthenticationPrincipal DemoUser user, Model model) {
		model.addAttribute("name", user.getEmail());
		return "private";
	}

	@GetMapping("/profile/{username}")
	public String user(@PathVariable String username, Model model) {
		try {
			var user = userDetailsService.loadUserByUsername(username);
			model.addAttribute("user", user);
			return "profile";
		}
		catch (UsernameNotFoundException e) {
			throw new ResponseStatusException(NOT_FOUND);
		}
	}

	@GetMapping("/admin")
	public String adminPage(@AuthenticationPrincipal DemoUser user, Model model) {
		model.addAttribute("name", user.getUsername());
		return "admin";
	}

	@GetMapping("/localhost")
	public String localhostPage(Model model) {
		model.addAttribute("pageName", "localhost");
		return "ok";
	}

	@GetMapping("/http-basic")
	public String basicPage(Model model) {
		model.addAttribute("pageName", "HTTP Basic");
		return "ok";
	}

	@GetMapping("/method/profile/{username}")
	public String methodProfile(@PathVariable String username, Model model) {
		var value = demoService.profile(username);
		model.addAttribute("pageName", "Method / Profile");
		model.addAttribute("value", value);
		return "ok";
	}

	@GetMapping("/method/corporate")
	public String methodVip(Authentication authentication, Model model) {
		var value = demoService.corporate(authentication.getName());
		model.addAttribute("pageName", "Method / VIP");
		model.addAttribute("value", value);
		return "ok";
	}

	@GetMapping("/method/allowed-domain")
	public String methodAllowList(Authentication authentication, Model model) {
		var value = demoService.emailAllowList(authentication.getName());
		model.addAttribute("pageName", "Method / Allow-list");
		model.addAttribute("value", value);
		return "ok";
	}

}

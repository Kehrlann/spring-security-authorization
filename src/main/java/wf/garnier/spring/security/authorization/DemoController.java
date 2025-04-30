package wf.garnier.spring.security.authorization;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class DemoController {

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/private")
	public String privatePage(@AuthenticationPrincipal DemoUser user, Model model) {
		model.addAttribute("name", user.getEmail());
		return "private";
	}

}

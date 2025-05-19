package wf.garnier.spring.security.authorization;

import wf.garnier.spring.security.authorization.user.DemoUser;
import wf.garnier.spring.security.authorization.user.DemoUserDetailsService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
class DemoController {

	private final ShipmentRepository shipmentRepository;
	private final DemoUserDetailsService demoUserDetailsService;

	DemoController(ShipmentRepository shipmentRepository, DemoUserDetailsService demoUserDetailsService) {
		this.shipmentRepository = shipmentRepository;
		this.demoUserDetailsService = demoUserDetailsService;
	}

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/private")
	public String privatePage(@AuthenticationPrincipal DemoUser user, Model model) {
		model.addAttribute("name", user.getEmail());
		try {
			var shipmentCount = shipmentRepository.count();
			model.addAttribute("shipmentCount", shipmentCount);
		}
		catch (Exception _) {

		}
		return "private";
	}

	@GetMapping("/admin")
	public String adminPage(@AuthenticationPrincipal DemoUser user, Model model) {
		model.addAttribute("name", user.getUsername());
		return "admin";
	}

	@GetMapping("/profile/{username}")
	public String profile(@PathVariable String username, Model model) {
		var user = demoUserDetailsService.loadUserByUsername(username);
		model.addAttribute("user", user);
		return "profile";
	}

}

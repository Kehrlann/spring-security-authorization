package wf.garnier.spring.security.authorization;

import wf.garnier.spring.security.authorization.user.DemoUserDetailsService;
import wf.garnier.spring.security.authorization.user.UserEmail;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
class DemoController {

	private final ShipmentRepository shipmentRepository;

	private final DemoUserDetailsService userDetailsService;

	DemoController(ShipmentRepository shipmentRepository, DemoUserDetailsService userDetailsService) {
		this.shipmentRepository = shipmentRepository;
		this.userDetailsService = userDetailsService;
	}

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/private")
	public String privatePage(@AuthenticationPrincipal UserEmail user, Model model) {
		model.addAttribute("name", user.getUserEmail());
		model.addAttribute("shipmentCount", shipmentRepository.count());
		return "private";
	}

	@GetMapping({ "/admin", "/admin-mfa" })
	public String adminPage(@AuthenticationPrincipal UserEmail user, Model model) {
		model.addAttribute("name", user.getUserEmail());
		return "admin";
	}

	@GetMapping({ "/profile/{username}", "/method-security/profile/{username}" })
	public String profile(@PathVariable String username, Model model) {
		var user = userDetailsService.findUser(username);
		model.addAttribute("user", user);
		return "profile";
	}

	@GetMapping("/corp")
	public String corporate(Authentication authentication, Model model) {
		model.addAttribute("pageName", "Corporate");
		model.addAttribute("value", "%s is part of Corp.".formatted(authentication.getName()));
		return "ok";
	}

	@GetMapping("/shipments")
	public String shipments(Model model) {
		model.addAttribute("shipments", shipmentRepository.findAll());
		return "shipments";
	}

	@GetMapping("/shipments/{id}")
	public String shipments(@PathVariable Integer id, Model model) {
		model.addAttribute("shipments", shipmentRepository.findById(id));
		return "shipments";
	}

	@GetMapping("/localhost")
	public String localhost(Model model) {
		model.addAttribute("pageName", "Localhost");
		model.addAttribute("value", "This page is only for 🏠Localhost");
		return "ok";
	}

	@GetMapping("/http-basic")
	public String httpBasic(Model model) {
		model.addAttribute("pageName", "HTTP Basic");
		model.addAttribute("value", "This page is only for 🤖 HTTP Basic");
		return "ok";
	}

	@GetMapping({ "/password", "/stronger-password" })
	public String passwordPage() {
		return "password";
	}

	@PostMapping("/password")
	public String updatePassword(Authentication authentication, String newPassword, Model model) {
		userDetailsService.updatePassword(authentication.getName(), newPassword);
		model.addAttribute("success", true);
		return "redirect:/private";
	}

}

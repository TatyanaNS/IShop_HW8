package com.ishop.controller;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	@Value("${myconfig.welcome.message}")
	private String welcomeMsg;
	@Value("${myconfig.welcome.img}")
	private String welcomeImg;
	@Value("${msg.title}")
	private String title;

	@GetMapping("/")
	public String mainPage(Authentication authentication,
			HttpServletResponse response,
			Map<String, Object> model) {
		model.put("userName", authentication.getName());
		model.put("welcomeMsg", welcomeMsg);
		model.put("welcomeImg", welcomeImg);
		model.put("title", title);
		return "main";
	}
}
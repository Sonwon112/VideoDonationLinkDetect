package com.niya.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.niya.model.WebDriverUnit;

@Controller
public class OverlayController {
	
	@Autowired
	WebDriverUnit unit;
	
	@PostMapping("/overlay")
	public ModelAndView overlayPage(String chatURL) {
		ModelAndView mav = new ModelAndView("overlay");
		mav.addObject("overlayLink", "146.56.102.79:8080/broadCastOverlay");
		unit.startOverlay(chatURL);
		
		return mav;
	}
	
	@GetMapping("/broadCastOverlay")
	public ModelAndView broadCastOverlay() {
		ModelAndView mav = new ModelAndView("broadCastOverlay");
		return mav;
	}
	
}

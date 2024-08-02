package com.niya.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class VotingController {
	
	@PostMapping("/voting")
	public String voting(String chatURL) {
		
		return "voting";
	}
}

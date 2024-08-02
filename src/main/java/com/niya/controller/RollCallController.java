package com.niya.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.niya.model.WebDriverUnit;
import com.niya.service.RollCallService;
import com.niya.service.SseService;

@Controller
@RequestMapping("/rollcall")
public class RollCallController {
	
	@Autowired
	WebDriverUnit unit;
	@Autowired
	RollCallService service;
	@Autowired
	SseService sse;
	
	@GetMapping(value={"/","/setWord","/setPartState"})
	public String wrongApproach() {
		return "redirect:/";
	}
	
	@PostMapping("/")
	public ModelAndView rollcall(String chatURL) {
		int id = service.createRollCallRoomAndSetWord(chatURL);
		ModelAndView mav = new ModelAndView("rollcall");
		mav.addObject("sseID", id);
		unit.startVoteOrRollCall(id,chatURL,"rollcall");
		return mav;
	}
	
	@PostMapping("/setWord")
	@ResponseBody
	public String setWord(int id, String word) {
		service.setWord(id, word);
//		System.out.println(id);
//		System.out.println(word);
		return "complete";
	}
	
	@PostMapping("/setPartState")
	@ResponseBody
	public String setPartState(int id, boolean state) {
		service.changePartState(id, state);
//		System.out.println(state);
		return "complete";
	}
	
	@PostMapping("/quit")
	@ResponseBody
	public String quit(int id) {
		service.quit(id);
		unit.close(id);
		sse.close(id);
//		System.out.println(state);
		return "complete";
	}
}

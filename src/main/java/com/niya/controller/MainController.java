package com.niya.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.niya.model.VideoDonation;
import com.niya.model.WebDriverUnit;
import com.niya.service.SseService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class MainController {
	
	public static List<VideoDonation> donationList = new ArrayList<VideoDonation>();
	@Autowired
	WebDriverUnit unit;
	@Autowired
	SseService service;
	
	@GetMapping("/")
	public String index() {
		return "index";
	}
	
	@GetMapping(value = "/list")
	public String list() {
		return "list";
	}
	
	@RequestMapping("/input")
	public ModelAndView inputLink(@RequestParam String link, @RequestParam String round) throws Exception{
		ModelAndView inputModelAndView = new ModelAndView("list");
		inputModelAndView.addObject("link", link);
		inputModelAndView.addObject("round", round);
		unit.start(link);
		
//		System.out.println(link);
		return inputModelAndView;
	}
	
	@RequestMapping("/end")
	public String end()throws Exception{
		unit.close(-1);
		//service.close();
		
		return "redirect:/";
	}
	
	@GetMapping("/export.do")
	public void callTxtFileDownload(HttpServletResponse response) {
		String docName="";
		try {
			docName = URLEncoder.encode("링크_리스트.txt","UTF-8").replaceAll("\\\\", "%20");
			response.setHeader("Content-Disposition", "attachment;filename="+docName+";");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/plain; charset=UTF-8");
			PrintWriter txtPrinter = response.getWriter();
			txtPrinter.println("순서&제목&링크");
			for(int i = 0; i < donationList.size(); i++) {
				String textLine = i+1 +"&"+donationList.get(i).getDonationName()+"&"+donationList.get(i).getDonationURL();
				txtPrinter.println(textLine);
			}
			response.flushBuffer();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

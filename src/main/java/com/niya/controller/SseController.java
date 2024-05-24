package com.niya.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.niya.service.SseService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class SseController {
	
	@Autowired
	private SseService sseService;
	public static String ROUND="";
	
	@GetMapping(value="/sse")
	public SseEmitter streamSseMvc(@RequestParam String round) {
		SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
		try {
			emitter.send(SseEmitter.event().name("connect"));
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		ROUND = round;
//		System.out.println(round);
		sseService.sseEmitters.put(round,emitter); // Repository를 만들었어야했는데 만들지 안았음
		
		emitter.onCompletion(()->sseService.sseEmitters.remove(round));
		emitter.onTimeout(()->sseService.sseEmitters.remove(round));
		emitter.onError((e)->sseService.sseEmitters.remove(round));
		
		return emitter;
	}
	
}

package com.niya.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class SseService {
	
	public Map<String,SseEmitter> sseEmitters = new ConcurrentHashMap<>();
	
	public void sendData(String round,String title,String link) {
		if (sseEmitters.containsKey(round)) {
			SseEmitter sseEmitter = sseEmitters.get(round);
		    try {
		    	sseEmitter.send(SseEmitter.event().name("alarm").data(
		       title+";"+link));
		        } catch (Exception e) {
		        	sseEmitters.remove(round);
		        }
		    }
	}
	
	public void sendData(String sseId, String nickName) {
		if(sseEmitters.containsKey(sseId)) {
			SseEmitter sseEmitter = sseEmitters.get(sseId);
			try {
				sseEmitter.send(SseEmitter.event().name("part").data(nickName));
			}catch (Exception e) {
				// TODO: handle exception
				sseEmitters.remove(sseId);
			}
		}
	}
	
	public void close() {
		sseEmitters.clear();
	}
	
	public void close(int id) {
		sseEmitters.remove(""+id);
	}
	
	
}

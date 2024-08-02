package com.niya.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.niya.repository.RollCallRepository;

@Service
public class RollCallService implements ChatService {
	
	@Autowired
	RollCallRepository repo;
	@Autowired
	SseService sse;
	
	public int createRollCallRoomAndSetWord(String url) {
		return repo.createRollCallRoomAndSetWord(url);
	}
	public void setWord(int id, String word) {
		repo.setWord(id, word);
	}

	public void changePartState(int id, boolean state) {
		repo.changePartState(id, state);
	}
	
	public int getParticipantSize(int id) {
		return repo.getParticipantSize(id);
	}
	
	public void quit(int id) {
		repo.quit(id);
	}
	
	@Override
	public void compare(int id, String nickname, String chat) {
		// TODO Auto-generated method stub
		String rollcallWord = repo.getRollCallWord(id);
		boolean partState = repo.isPart(id);
		if(partState && chat.contains(rollcallWord)) {
			if(!repo.checkNickname(id, nickname)) {
				repo.appedNickname(id, nickname);
				sse.sendData(""+id, nickname);
//				System.out.print(nickname);
			}
		}
	}
	
}

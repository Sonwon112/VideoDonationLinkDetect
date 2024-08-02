package com.niya.repository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.niya.model.RollCallRoom;

@Repository
public class RollCallRepository {
	
	private Map<Integer,RollCallRoom> rollcallRoomMap = new HashMap<>();
	private int id = 0;
	
	public int createRollCallRoomAndSetWord(String url) {
		id++;
		RollCallRoom tmp = new RollCallRoom(url);
		rollcallRoomMap.put(id, tmp);
		return id;
	}
	
	public void setWord(int id, String word) {
		RollCallRoom tmp = rollcallRoomMap.get(id);
		tmp.setRollcallWord(word);
	}
	
	public void changePartState(int id, boolean state) {
		RollCallRoom tmp = rollcallRoomMap.get(id);
		tmp.setPartState(state);
	}
	
	public void appedNickname(int id, String nickname) {
		RollCallRoom tmp = rollcallRoomMap.get(id);
		tmp.appendParticipant(nickname);
	}
	
	public void quit(int id) {
		rollcallRoomMap.remove(id);
	}
	
	
	public String getRollCallWord(int id) {
		RollCallRoom tmp = rollcallRoomMap.get(id);
		return tmp.getRollcallWord();
	}
	
	public int getParticipantSize(int id) {
		RollCallRoom tmp = rollcallRoomMap.get(id);
		return tmp.getParticipantListSize();
		
	}
	
	public boolean checkNickname(int id, String nickname) {
		RollCallRoom tmp = rollcallRoomMap.get(id);
		return tmp.checkNickname(nickname);
	}
	
	public boolean isPart(int id) {
		RollCallRoom tmp = rollcallRoomMap.get(id);
		return tmp.isPartState();
	}
	
}

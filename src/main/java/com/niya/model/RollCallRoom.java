package com.niya.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RollCallRoom {
	private String rollcallWord;
	private String url;
	private boolean partState = false;
	private List<String> participantList = new ArrayList<>();
	
	public RollCallRoom(){}
	public RollCallRoom(String url) {
		this.url = url;
	}
	
	public void appendParticipant(String nickname) {
		participantList.add(nickname);
	}
	
	public String getParticipant(int index) {
		return participantList.get(index);
	}
	
	public int getParticipantListSize() {
		return participantList.size();
	}
	
	public boolean checkNickname(String nickname) {
		return participantList.contains(nickname);
	}
}

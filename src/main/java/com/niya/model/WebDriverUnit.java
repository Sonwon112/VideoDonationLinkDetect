package com.niya.model;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.niya.controller.MainController;
import com.niya.controller.SseController;
import com.niya.service.ChatService;
import com.niya.service.RollCallService;
import com.niya.service.SseService;
import com.niya.service.VotingService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WebDriverUnit {
	
	private static String WEB_DRIVER_ID="webdriver.chrome.driver";
	@Value("${web_driver_path}")
	private String WEB_DRIVER_PATH;

	
	Map<Integer,WebDriver> webdriverMap = new HashMap<Integer, WebDriver>();
	Map<Integer,Timer> timerMap = new HashMap<Integer, Timer>();
	
	@Autowired
	private SseService sseService;
	@Autowired
	private RollCallService rollCallService;
	@Autowired
	private VotingService votingService;
	
	public WebDriverUnit() {}
	
	/**
	 * 빙플리시 영상 후원 크롤링 시 사용
	 * @param base_url
	 * @throws IOException 
	 */
	public void start(String base_url) throws IOException {
//		this.base_url = base_url;
		File f = new File(WEB_DRIVER_PATH);
		if(!f.exists()) {
			String userDirectoryPath = System.getProperty("user.dir");
			log.info("can't found driver. try Find that directory ("+userDirectoryPath+")");
			WEB_DRIVER_PATH = userDirectoryPath + "/chromedriver-win64/chromedriver.exe";
			f = new File(WEB_DRIVER_PATH);
			if(!f.exists()) {
				log.error("can't found driver");
				throw new IOException();
			}
		}
		
	
		System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
		
		WebDriver webdriver = new ChromeDriver();
		webdriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		webdriverMap.put(-1, webdriver);
		crawl(webdriver, base_url);
		
		Timer timer = new Timer();
		timer.schedule(new crawlTask(webdriver,sseService), 0, 1000);
		timerMap.put(-1, timer);
	}
	/**
	 * 빙플리시 영상 후원 크롤링 시 사용
	 * @param base_url
	 */
	public void startOverlay(String base_url) {
//		this.base_url = base_url;
		System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
		
		WebDriver webdriver = new ChromeDriver();
		webdriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		webdriverMap.put(-2, webdriver);
		crawl(webdriver, base_url);
		
		Timer timer = new Timer();
		timer.schedule(new crawlCheeze(webdriver,sseService), 0, 1000);
		timerMap.put(-2, timer);
	}
	
	/**
	 * 투표, 점호 기능 사용시 크롤링하는 메소드
	 * @param base_url
	 */
	public void startVoteOrRollCall(int id, String base_url, String mode) {
//		this.base_url = base_url;
		System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
		
		WebDriver webdriver = new ChromeDriver();
		webdriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		
		webdriverMap.put(id, webdriver);
		
		crawl(webdriver,base_url);
		ChatService chat = null;
		switch (mode) {
			case "rollcall": 
				chat = rollCallService;
				break;
			case "vote":
				chat = votingService;
				break;
		}
		
		
		Timer timer = new Timer();
		timer.schedule(new crawlChatTask(id,webdriver,chat), 0, 1000);
	}
	
	public void crawl(WebDriver webdriver, String base_url) {
		try {
			webdriver.get(base_url);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	
	public void close(int id) {
		try {
			WebDriver tmp = webdriverMap.get(id);
			tmp.close();
			Timer timer = timerMap.get(id);
			timer.cancel();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
}

class crawlChatTask extends TimerTask{
	
	private WebDriver webdriver;
	private ChatService chatService;
	
	private int id;
	
	private final String nickNameTag = "name_text__yQG50";
	private final String chatTag = "live_chatting_message_text__DyleH";
	
	
	public crawlChatTask(int id,WebDriver webdriver, ChatService chatService) {
		this.webdriver = webdriver;
		this.chatService = chatService;
		this.id = id;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		String source = webdriver.getPageSource();
		
		//live_chatting_message_text__DyleH 채팅 div class
		//name_text__yQG50 닉네임 span class
		int startPos = source.indexOf(nickNameTag);
		
		while(startPos != -1) {
			try {
				source = source.substring(startPos);
				startPos = source.indexOf(">");
				int endPos = source.indexOf("</span>");
				String nickname = source.substring(startPos+1, endPos);
				
				startPos = source.indexOf(chatTag);
				source = source.substring(startPos);
				
				startPos = source.indexOf(">");
				endPos = source.indexOf("</span>");
				String chat = source.substring(startPos+1, endPos);
				
				startPos = source.indexOf(nickNameTag);
					
				chatService.compare(id, nickname, chat);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
}

class crawlTask extends TimerTask{
	
	private WebDriver webdriver;
	private String prevDonation = "";
	private SseService sseService;
	
	
	public crawlTask(WebDriver webdriver, SseService sseService) {
		this.webdriver = webdriver;
		this.sseService = sseService;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		String source = webdriver.getPageSource();
		if(source.contains("<iframe")) {
			int startPoint = source.indexOf("<iframe");
			int endPoint = source.indexOf("</iframe>");
			source = source.substring(startPoint,endPoint);
			
			if(prevDonation.equals(source)) return;
			else prevDonation = source;
			
			int titleStartPoint = source.indexOf("title");
			int titleEndPoint = source.indexOf("width");
			
			String title = source.substring(titleStartPoint+7,titleEndPoint-2);
			
			title = title.replace("&quot;", "\"");
			title = title.replace("&amp;", "&");
			
			
			int srcStartPoint = source.indexOf("embed/");
			int srcEndPoint = source.indexOf("?", srcStartPoint);
			
			String url = source.substring(srcStartPoint+6,srcEndPoint);
			url = "www.youtube.com/watch?v="+url;
			if(!title.contains("YouTube video player")) {
				MainController.donationList.add(new VideoDonation(title,url));
				sseService.sendData(SseController.ROUND, title, url);
			}
		}
	}
	
}

class crawlCheeze extends TimerTask{
	
	private WebDriver webdriver;
	private String prevChat = "";
	private SseService sseService;
	
	static final String chatListStart = "<div class=\"live_chatting_list_wrapper__a5XTV\">";
	static final String chatListEnd = "</div><div class=\"live_chatting_area__hUPJw\">";
	
	static final String cheezeDiv = "live_chatting_list_item__0SGhw live_chatting_list_donation__Fy6Vz";
	static final String cheezeChatbox = "<p class=\"live_chatting_donation_message_text__XbDKP\">";
	static final String cheezeMoney = "live_chatting_donation_message_money__fE2UC";
	
	private List<String> donationMessage = new ArrayList<String>();
	
	public crawlCheeze(WebDriver webdriver, SseService sseService) {
		this.webdriver = webdriver;
		this.sseService = sseService;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		String source = webdriver.getPageSource();
		
		//if(source.contains("공일이")) {sseService.sendOverlayState();}
			if(source.contains(cheezeDiv)) {
				int startPos = source.indexOf(cheezeDiv);
				source = source.substring(startPos);
				int pos = 0;
				while(startPos != -1) {
					
					startPos = source.indexOf(cheezeChatbox);
					if(startPos == -1)break;
					source = source.substring(startPos+cheezeChatbox.length());
					
					int endPos = source.indexOf("</p>");
					String text = source.substring(0,endPos);
					
					if(donationMessage.size() <= pos) {
						//텍스트 조건
						if(text.contains("!쓰담")) {
							sseService.sendOverlayState();
						}
						donationMessage.add(text);
					}else {
						if(!donationMessage.get(pos).equals(text))
							donationMessage.set(pos, text);
					}
					pos++;
					startPos = source.indexOf(cheezeDiv);
					
				}
				
			}
		
		

	}
	
}

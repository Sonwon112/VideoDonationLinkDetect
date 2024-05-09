package com.niya.model;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.niya.controller.MainController;
import com.niya.controller.SseController;
import com.niya.service.SseService;

@Component
public class WebDriverUnit {
	
	private static String WEB_DRIVER_ID="webdriver.chrome.driver";
	private static String WEB_DRIVER_PATH="C:/workspace/java library/chromedriver-win64/chromedriver.exe";
	
	private WebDriver webdriver;
	
	private String base_url;
	
	private Timer timer;
	
	@Autowired
	private SseService sseService;
	
	public WebDriverUnit() {}
	
	public void start(String base_url) {
		this.base_url = base_url;
		System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
		
		webdriver = new ChromeDriver();
		webdriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		
		crawl();
		
		timer = new Timer();
		timer.schedule(new crawlTask(webdriver,sseService), 0, 1000);
	}
	
	public void crawl() {
		try {
			webdriver.get(base_url);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			timer.cancel();
			webdriver.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
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

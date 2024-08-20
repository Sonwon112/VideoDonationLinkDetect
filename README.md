# 셀레니움을 통한 치지직 영상후원 링크 추출 프로그램

### 개요

- 참여인원 : 1인
- 제작 기한 : 24.04.29 ~ 24.05.06
- 제작 동기 : 
방송용 컨텐츠에서 노래 관련 영상 후원을 받은 내역에 대해 링크를 추출하여 플레이 리스트를 만들어야하는데 현재 ‘치지직’이라는 플랫폼에서는 스트리머가 후원 내역 관리를 통해 영상 링크를 보기 어렵게 되어있어 일일히 들어가서 링크를 복사 붙여넣기 해야했습니다. 불편한 점을 발견하였고 이를 간편하게 하기 위해 영상 후원 링크를 통해 크롤링하여 화면에 표시되는 영상  후원

### 사용 언어 및 프레임워크

- Java
- Spring boot

### 사용한 기술

- Selenium
- SSE

### 클래스 설계 및 알고리즘

→ 클래스 다이어그램 및, 알고리즘 순서도 추가

### 진행 과정

- 팀으로 활동중인 스트리머의 컨텐츠 중 치지직이라는 방송 플랫폼에서 시청자들이 추천하는 노래 영상 후원을 받고 이 리스트를 정리를 해야하는데, 치지직에서 기존에 제공하는 영상후원 관리페이지에서는 영상 링크가 아닌 영상 제목을 제공하고 제목을 통해 해당 링크로 접속할 수 있게하여서 스트리머 분이 일일이 링크를 복사하고 제목을 복사한다고 하여서 지금의 자동 링크 추출 사이트를 제작하게 되었습니다.
- 우선 Model과 Controller를 분리하고 View 페이지는 JSP로 구성하였습니다. 그리고 Service도 따로 분리하여 구현하였습니다
(Repository도 분리하여 Service에서 Map을 분리하여 구현하면 좋았을 거 같습니다.)
- 처음에는 통신방식 중에 SSE와 WebSocket 방식을 알지 못하였습니다. 그래서 알고 있던 Polling 방식을 통해 클라이언트가 서버에게 일정시간 마다 요청을 보내고 이에 대해 응답하는 방식을 사용하려고 하였는데 이 방식은 실시간 처리를 위해서는 클라이언트 측에서 일정시간마다 요청을 보내야하는 비효율적인 방법이라고 생각을 하였습니다. 
  마침, 카카오톡이나, SNS의 다이렉트 메시지, SNS의 좋아요 알림은 서버에서 클라이언트 측에게 정보를 전송하는 것이라는 글을 읽었던 기억이 나서 인터넷 검색을 통해 조사를 해보니 Polling 방식 뿐아니라 Web 통신 방식 중에는 SSE와 WebSocket 방식이 있다는 것을 알게 되었습니다. 
  현재 웹 페이지는 클라이언트와 서버 간의 데이터를 주고 받는 것이 아닌, 클라이언트와 서버가 연결 되면 서버에서 데이터가 갱신 되었을 때 클라이언트에 전송하고 클라이언트가 갱신하면 되는 방식이기 때문에 SSE 방식을 통한 통신을 체택하였습니다.

※ 채팅을 통한 점호 시스템도 부가적으로 추가하였습니다.

### 핵심 코드 및 알고리즘

- 셀레니움 코드

사용자가 영상후원 오버레이 페이지 링크를 입력하면 웹 드라이버를 통해 해당 링크의 페이지 정보를 Timer를 통해 1초에 한번씩 읽어오게 됩니다. 그리고 추출해야하는 데이터가 생성되었을 때 추출 데이터를 SSEService의 send 메서드를 통해 클라이언트에게 전달하게 됩니다. 

```jsx
//사용자 영상후원 링크 입력시 셀레니움을 통해 페이지 크롤링 시작
public void start(String base_url) {
//		this.base_url = base_url;
		System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
		
		WebDriver webdriver = new ChromeDriver();
		webdriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		webdriverMap.put(-1, webdriver);
		crawl(webdriver, base_url);
		
		Timer timer = new Timer();
		timer.schedule(new crawlTask(webdriver,sseService), 0, 1000);
		timerMap.put(-1, timer);
}
	
public void crawl(WebDriver webdriver, String base_url) {
		try {
			webdriver.get(base_url);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
//지정된 시간마다 페이지를 크롤링하는 Thread
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
```
![KakaoTalk_20240817_152032999_01](https://github.com/user-attachments/assets/4b09d581-106a-4d84-aad7-171a7592bc66)



- SSE 코드

 클라이언트가 링크와 해당 회차를 입력하게되면 서버에는 링크와 회차가 입력하면 셀레니움에 해당 회차에 해당 웹드라이버가 생성되게 되고 클라이언트 측 javascript에서 eventSource를 통해 SSEEmitter를 생성하는 컨트롤러에 요청을 보내게 됩니다.

 SSEEmitter가 생성되면 웹 드라이버에서 크롤링을 수행하면서 추출한 링크와 제목 정보를 SSEEmitter를 통해 사용자에게 전송하게 되고 사용자 페이지에는 리스트가 표시되게 됩니다.

```java
// Controller
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
		sseService.sseEmitters.put(round,emitter); 
		// Repository를 만들었어야했는데 만들지 안았음, 함수가 아닌 필드에 접근하였음
		
		emitter.onCompletion(()->sseService.sseEmitters.remove(round));
		emitter.onTimeout(()->sseService.sseEmitters.remove(round));
		emitter.onError((e)->sseService.sseEmitters.remove(round));
		
		return emitter;
	}
	
	//Service
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
```

```jsx
// 사용자가 동영상 오버레이 링크와 회차를 넣은 후에 SSE 이벤트 소스를 등록하여서 서버와 통신
const eventSource = new EventSource("/sse?round="+'${round}')
				eventSource.addEventListener("alarm", (event)=>{
					let [title,link] = event.data.split(";");
					//console.log(title);
					//console.log(link);
					if(!title.includes("YouTube video player")){
						//console.log(title);
						//console.log(link);
						
						$("#linkTable").append("<tr>"+
													"<td>"+count+"</td>"+
													"<td>${round}</td>"+
													"<td>"+title+"</td>"+
													"<td>"+link+"</td>"+
											   "</tr>");
						count+=1;
					}
					
				});
```

![KakaoTalk_20240817_152032999](https://github.com/user-attachments/assets/9ac60f9f-b201-4dbf-bd05-dbc9446b34d5)


- .txt 파일 추출

링크를 자동으로 추출하여 사이트에 표시했다면 컨텐츠가 끝나고 정리된 정보를 txt 파일로 추출하여야 하기 때무

```java
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
```

### 진행하면서 어려웠던 점 및 느낀점

- 우선 Selenium이라는 API를 처음 사용해 보았어서 초반에 메이븐으로 Selenium jar파일 추가하였을 때 dependency 오류가 발생하여서 직접 라이브러리를 프로젝트에 포함 시키면서 이 문제를 해결하였습니다. 
  그러나, 메이븐을 통해 war 파일을 빌드하려고 할 때, 직접 라이브러리로 프로젝트에 포함 시킨 Selenium 라이브러리를 찾을 수 없다는 오류가 발생하여 빌드는 하지 못하지만 동작은 가능하게 하여 배포단계에서 어려움을 겪고 있습니다. 이 문제를 해결하기 위해 꾸준히 다양한 방법을 모색하고 있습니다.
- 테스트 진행 단계에서 사용자에게 사용하는 링크를 알려주었지만, 잘못된 링크로 시도하여서 문제가 되었던 경험을 통해 사용하는 링크가 지정되어있다면 링크의 양식이 맞지 않았을 때의 방안을 제시해야하는 점, 그리고 사용자의 이해를 돕기 위해선 말보다는 글, 글보다는 그림을 통한 도움말을 제시해야한다는 것을 알 수 있었습니다.
    
    모든 프로그램은 사용자의 입장에서 편해야하고, 친절해야한다는 것을 느꼈습니다.

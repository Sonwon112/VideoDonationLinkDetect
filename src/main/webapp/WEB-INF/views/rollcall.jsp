<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>점호!</title>
<link href="/resources/css/rollcall.css" rel="stylesheet" />

</head>
<body>
	<h1>점호</h1>
	<hr>
	<button onclick="quit()">나가기</button>
	<br/>
	<input id="inputRollCallWord" type="text" placeholder="점호 단어"/>
	<input id="btnRollCallWord" type="button" value="단어 설정"/>
	<br>
	<div class="inline-block">
						<div class="left">
							<input type="checkbox" id="toggle" hidden onclick="changeParticipantState()">
							<label for="toggle" class="toggleSwitch">
								<span class="toggleButton"></span>
							</label>
						</div>
						<div class="left" id="participantState">비활성화</div>
					</div>
	<div id="participantList">
		
	</div>
	<div id="paging">
		<button id="btnLeft" onclick="left()"> ◀ </button>
		<div id="divCnt">0/0</div>
		<button id="btnRight" onclick="right()"> ▶ </button>
	</div>
	
	<script src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
	<script type="text/javascript">
		let sseId = '${sseID}';		
	</script>
	<script src="/resources/js/rollcall.js"></script>
</body>
</html>
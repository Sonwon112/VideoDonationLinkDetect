<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>냐툴</title>
</head>
<body>
	<h1>니야님의, 니야님에 의한, 니야님을 위한 도구 사이트</h1>
	<div>※url 입력창의 정보는 공개되지 않으나 url이 공개되지 않게 점호 페이지를 제외하고는 공개를 안하시길 바랍니다.※</div>
	<hr>
	<form action="/list" method="get">
		<input type="submit" value="빙플리 추출하기"/>
	</form>
	<form id="chattingForm" method="post">
		<input type="password" name="chatURL" placeholder="채팅창 '오버레이 url' 입력"/>
		<!--<input type="button" value="투표하기" onclick="chatMode('voting')"/>-->
		<input type="button" value="점호하기" onclick="chatMode('rollcall')"/>
	</form>
	<form id="overlayForm" method="post">
		<input type="password" name="chatURL" placeholder="채팅창 '오버레이 url' 입력"/>
		<!--<input type="button" value="투표하기" onclick="chatMode('voting')"/>-->
		<input type="button" value="오버레이 관리" onclick="chatMode('overlay')"/>
	</form>
	
	<script src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
	<script src="/resources/js/func.js"></script>
</body>
</html>
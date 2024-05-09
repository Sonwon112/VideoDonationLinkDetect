<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>빙플리 링크 추출 사이트</title>
<script src="//code.jquery.com/jquery-3.3.1.min.js"></script>
<style type="text/css">
	input{
		font-size:1.2rem;
	}
	#roundInput{
		width : 10%
	}
	
	thead{
		font-style:bold;
	}
	td{
		border : black 2px solid
	}
</style>
</head>
<body>
	<form method="post" action="/input">
		<input id="roundInput" type="text" name="round" placeholder="회차" value=${round}>
		<input id="linkInput" type="text" name="link" placeholder="링크 주소를 입력해주세요"
			value=${link}>
		<button id="inputBtn" type="submit">입력</button>
	</form>
	<form method="post" action="/end">
		<button type="submit">종료</button>
	</form>
	<form method="get" action="/export.do">
		<button type="submit">추출</button>
	</form>
	<table>
		<thead>
			<tr>
				<td>순서</td>
				<td>회차</td>
				<td>제목</td>
				<td>링크</td>
			</tr>
		</thead>
		<tbody id="linkTable">
			
		</tbody>
	</table>
	
	
	<script>
		$(document).ready(()=>{
			let count = 1;
			let linkText = '${link}';
			if(linkText != ""){
				$("#inputBtn").attr('disabled',true);
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
			}else{
				$("#inputBtn").attr('disabled',false);
			}
		});
	</script>

</body>
</html>
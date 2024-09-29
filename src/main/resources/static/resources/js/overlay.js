let cnt = 0;
$(document).ready(()=>{
	
    const eventSource = new EventSource("/sse?round=overlay");
    eventSource.addEventListener("showImage",(event)=>{ 
		let state = event.data;
		
		if(state=="true"){
			cnt++;
			//console.log(cnt);
			
			//callFadeOut();
		}
	});
	
	setInterval(function(){
		if(cnt > 0){
			callFadeOut();
			cnt--;
			//console.log(cnt);	
		}
		
	},3000);
});

function callFadeOut(){
	$(".handImg").css("display","");
	$(".handImg").fadeOut(2000);
}


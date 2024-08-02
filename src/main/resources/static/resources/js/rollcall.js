let MAX_PAGE_SIZE = 15; // 페이지당 15개
let participantSize = 0;
let currCntSize = 0;
let currPageLast = -1;
let page = 1;

let participantList = [];
let selectedList = [];

$(document).ready(()=>{
    const eventSource = new EventSource("/sse?round="+sseId);
    eventSource.addEventListener("part",(event)=>{
        let nickName = event.data;
		if(currPageLast < (MAX_PAGE_SIZE*page)-1){
			let id = participantSize;
			$('#participantList').append("<div class='list' id="+id+" onclick='selected(\""+id+"\")'> &ensp; "+nickName+" &ensp; </div>");	
			currPageLast++;
		}
        participantList.push(nickName);
		selectedList.push(0);
		participantSize++;
		$('#divCnt').text(currCntSize+"/"+participantSize);
	});

    $('#btnRollCallWord').on("click", function(e){
        let formData = new FormData();
        formData.append("id",sseId);
        formData.append("word",$('#inputRollCallWord').val());

        $.ajax({
            url:"/rollcall/setWord",
            processData: false,
            contentType: false,
            data: formData,
            type: 'POST',
            success: function(result){
                alert("단어가 설정되었습니다.");
            }
        });
    });


});
function changeParticipantState() {
    let isChecked = $("#toggle").is(":checked");
    switch (isChecked) {
        case true:
            $("#participantState").text("활성화 중...")
            break;
        case false:
            $("#participantState").text("비활성화")
            break;
    }

    let formData = new FormData();
    formData.append("id",sseId);
    formData.append("state",isChecked);
    $.ajax({
        url:"/rollcall/setPartState",
        processData: false,
        contentType: false,
        data: formData,
        type: 'POST',
        success: function(result){
            //console.log("모드 변경 완료");
        }
    });
}

function selected(nickname){
	let id = "#"+nickname;
	if($(id).hasClass("selected") == true){
		$(id).removeClass("selected");
		currCntSize = currCntSize > 0 ? currCntSize - 1:0;
		selectedList[Number(nickname)] = 0;
	}
	else{
		$(id).addClass("selected");
		currCntSize = currCntSize < participantSize ? currCntSize + 1:participantSize;
		selectedList[Number(nickname)] = 1;
	}
	$('#divCnt').text(currCntSize+"/"+participantSize);
	//console.log(selectedList);
}

function quit(){
	
	let formData = new FormData();
    formData.append("id",sseId);
	$.ajax({
        url:"/rollcall/quit",
        processData: false,
        contentType: false,
        data: formData,
        type: 'POST',
        success: function(result){
            //console.log("모드 변경 완료");
			history.back();
        }
    });
}



function left(){
	if(currPageLast - MAX_PAGE_SIZE >= 0){
		let first = currPageLast-MAX_PAGE_SIZE < MAX_PAGE_SIZE? 0 : (page-2)*MAX_PAGE_SIZE;
		$('#participantList').empty();
		for(let i = first; i < first+MAX_PAGE_SIZE; i++){
			if(participantList[i]==undefined)continue;
			$('#participantList').append("<div class='list' id="+i+" onclick='selected(\""+i+"\")'> &ensp; "+participantList[i]+" &ensp; </div>");
			if(selectedList[i] == 1){
				let id = "#"+i;
				$(id).addClass("selected");
			}
			currPageLast = i;
		}
		page--;
	}
	//console.log(currPageLast);
}

function right(){
	if(currPageLast + 1 < participantSize){
		let last = currPageLast + 1 +MAX_PAGE_SIZE > participantSize ? participantSize : currPageLast + 1 + MAX_PAGE_SIZE;
		$('#participantList').empty();
		for(let i = currPageLast + 1; i < last; i++){
			$('#participantList').append("<div class='list' id="+i+" onclick='selected(\""+i+"\")'> &ensp; "+participantList[i]+" &ensp; </div>");
			if(selectedList[i] == 1){
				let id = "#"+i;
				$(id).addClass("selected");
			}
			currPageLast=i;
		}
		page++;
	}
	//console.log(currPageLast);
}

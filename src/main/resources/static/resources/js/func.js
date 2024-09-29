/**
 * 
 */

function chatMode(mode){
    switch(mode){
        case 'voting':
            $('#chattingForm').attr("action","/voting").submit();
            break;
        case 'rollcall':
            $('#chattingForm').attr("action","/rollcall/").submit();
            break;
		case 'overlay':
			$('#overlayForm').attr("action","/overlay").submit();
			break;
    }
}
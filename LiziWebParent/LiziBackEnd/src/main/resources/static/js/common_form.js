$(document).ready(function(){
	$("#buttonCancel").on("click", function(){
		window.location = moduleURL;
	});										
	
	$("#fileImage").change(function(){
		fileSize = this.files[0].size;									
		if (fileSize > MAX_FILE_SIZE) {
			this.setCustomValidity("You must choose an image less than " + MAX_FILE_SIZE + " bytes!");
			this.reportValidity();
		} else {
			this.setCustomValidity("");
			showImageThumbnail(this);
		}
						
	});												
});
		
function showImageThumbnail(fileInput){
	var file = fileInput.files[0];
	var render = new FileReader();
	render.onload = function(e) {
		$("#thumbnail").attr("src", e.target.result);
	};
	
	render.readAsDataURL(file);
}

function showModalDialog(title, message){
	$("#modalTitle").text(title);			
	$("#modalBody").text(message);
	$("#modalDialog").modal('show');
}

function showErrorModal(message) {
	showModalDialog("Lỗi", message);
}

function showWarningModal(message) {
	showModalDialog("Cảnh báo", message);
}
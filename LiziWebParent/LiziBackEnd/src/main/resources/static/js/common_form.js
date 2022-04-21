$(document).ready(function(){
	$("#buttonCancel").on("click", function(){
		window.location = moduleURL;
	});										
	
	$("#fileImage").change(function(){
		fileSize = this.files[0].size;							
		// < 1MB
		if(fileSize > 102400) { 
			this.setCustomValidity("Bạn phải chọn ảnh có kích thước nhỏ hơn 100KB");
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
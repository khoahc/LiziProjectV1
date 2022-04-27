function clearFilter() {
	window.location = moduleURL;	
}

function showDeleteConfirmModal(link, entityName) {
	entityId = link.attr("entityId");
	
	$("#yesButton").attr("href", link.attr("href"));	
	$("#confirmText").text("Bạn có muốn xóa "
							 + entityName + " có ID " + entityId + " không ?");
	$("#confirmModal").modal('show');	
}
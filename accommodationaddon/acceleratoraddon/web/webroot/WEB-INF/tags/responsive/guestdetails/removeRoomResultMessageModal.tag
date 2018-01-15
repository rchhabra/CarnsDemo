<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<div class="modal fade" id="y_removeRoomResultMessageModal" tabindex="-1" role="dialog" aria-labelledby="removeRoomResultMessageModal">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header clearfix">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title" id="removeRoomResultMessageModal">
					<spring:theme code="text.guest.details.remove.room.modal.title" text="Remove Room" />
				</h4>
			</div>
			<div class="modal-body">
				<div class="y_removeRoomResultMessageModalBody">
				</div>
			</div>
		</div>
	</div>
</div>

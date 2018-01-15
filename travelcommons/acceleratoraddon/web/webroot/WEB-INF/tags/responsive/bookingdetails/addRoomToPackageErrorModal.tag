<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="modal fade" id="y_addRoomToPackageErrorModal" tabindex="-1" role="dialog" aria-labelledby="addRoomToPackageErrorModal">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title" id="addRoomToPackageErrorModal">
					<spring:theme code="error.page.bookingdetails.add.room.package.modal.title" text="Add Room to Package Error" />
				</h4>
			</div>
			<div class="modal-body">
				<div id="y_addRoomToPackageErrorBody"></div>
			</div>
		</div>
	</div>
</div>

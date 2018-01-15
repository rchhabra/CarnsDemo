<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="modal fade y_ratePlanRoomSelectStockErrorModal" tabindex="-1" role="dialog" aria-labelledby="ratePlanRoomSelectStockErrorModal">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title">
					<spring:theme code="text.accommodation.details.add.accommodation.to.cart.error.modal.title" text="Error with selected accommodation" />
				</h4>
			</div>
			<spring:theme var="errorModalBodyMsg" code="text.accommodation.details.ratePlan.roomselect.stocklevel.error.modal.message" text="There are only {0} number of this room. Please remove one from '{1}' rate plan to proceed." />
			<div class="modal-body">
				<div class="y_ratePlanRoomSelectStockErrorModalBody" data-errormodalbodymsg="${fn:escapeXml(errorModalBodyMsg)}">
				</div>
			</div>
		</div>
	</div>
</div>

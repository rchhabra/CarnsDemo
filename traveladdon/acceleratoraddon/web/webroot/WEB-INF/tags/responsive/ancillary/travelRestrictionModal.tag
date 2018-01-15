<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<div class="modal fade" id="y_travelRestrictionModal" tabindex="-1" role="dialog" aria-labelledby="travelRestrictionModaLabel">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title" id="travelRestrictionModaLabel">
					<spring:theme code="text.ancillary.travel.restriction.modal.title" text="Travel Restriction" />
				</h4>
			</div>
			<div class="modal-body">
				<h3>
					<spring:theme code="text.ancillary.travel.restriction.modal.subtitle" />
				</h3>
				<div class="y_travelRestrictionErrorBody">
				</div>
			</div>
		</div>
	</div>
</div>

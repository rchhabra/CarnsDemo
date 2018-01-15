<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<div class="modal fade" id="y_validateCartErrorModal" tabindex="-1" role="dialog" aria-labelledby="validateCartErrorModal">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title" id="validateCartErrorModal">
					<spring:theme code="text.accommodation.details.validate.cart.error.modal.title" text="Please select accommodation" />
				</h4>
			</div>
			<div class="modal-body">
				<spring:theme code="text.accommodation.details.validate.cart.error.modal.text" text="Please select an accommodation before proceeding to the next page." />
			</div>
		</div>
	</div>
</div>

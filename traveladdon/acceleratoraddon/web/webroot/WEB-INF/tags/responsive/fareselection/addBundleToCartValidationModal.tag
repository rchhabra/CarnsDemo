<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="modal fade" id="y_addBundleToCartValidationModal" tabindex="-1" role="dialog" aria-labelledby="addBundleToCartValidationModalLabel">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title" id="addBundleToCartValidationModalLabel">
					<spring:theme code="fareselection.validation.incorrect" />
				</h4>
			</div>
			<div class="modal-body">
				<p class="y_addBundleToCartValidationBody" >
					<spring:theme code="add.bundle.to.cart.request.error" />
				</p>
			</div>
		</div>
	</div>
</div>

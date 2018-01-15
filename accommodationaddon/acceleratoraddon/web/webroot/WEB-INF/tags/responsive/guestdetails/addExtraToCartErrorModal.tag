<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<div class="modal fade" id="y_addExtraToCartErrorModal" tabindex="-1" role="dialog" aria-labelledby="addExtraToCartErrorModal">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title" id="addExtraToCartErrorModal">
					<spring:theme code="text.guest.details.add.extra.to.cart.error.modal.title" text="" />
				</h4>
			</div>
			<div class="modal-body">
				<div class="y_addProductToCartErrorBody"></div>
			</div>
		</div>
	</div>
</div>

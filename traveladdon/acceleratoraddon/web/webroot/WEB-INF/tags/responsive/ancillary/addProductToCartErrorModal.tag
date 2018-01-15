<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<div class="modal fade" id="y_addProductToCartErrorModal" tabindex="-1" role="dialog" aria-labelledby="addProductToCartErrorModal">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title" id="addProductToCartErrorModal">
					<spring:theme code="text.ancillary.add.product.to.cart.error.modal.title" text="" />
				</h4>
			</div>
			<div class="modal-body">
				<div class="y_addProductToCartErrorBody">
				</div>
			</div>
		</div>
	</div>
</div>

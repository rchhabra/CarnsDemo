<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div class="modal fade" id="y_addProductToCartErrorWithOKModal" tabindex="-1" role="dialog" aria-labelledby="addProductToCartErrorWithOKModal" data-backdrop="static" data-keyboard="false">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title" id="addProductToCartErrorWithOKModal">
					<spring:theme code="text.ancillary.add.product.to.cart.error.modal.title" text="" />
				</h4>
			</div>
			<div class="modal-body">
				<div class="y_addProductToCartErrorWithOKBody"></div>
			</div>
			<div class="modal-footer">
				<div class="row">
					<div class="col-xs-12 col-sm-6">
						<a class="btn btn-secondary btn-block" href="javascript:location.reload()">
							<spring:theme code="text.ancillary.add.product.to.cart.error.modal.ok" text="OK" />
						</a>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

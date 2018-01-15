<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<div class="modal fade" id="y_addPackageToCartErrorModal" tabindex="-1" role="dialog" aria-labelledby="addPackageToCartErrorModal">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title" id="addPackageToCartErrorModal">
					<spring:theme code="text.package.details.add.package.to.cart.error.modal.title" text="Add package to cart error" />
				</h4>
			</div>
			<div class="modal-body">
				<div class="y_addPackageToCartErrorBody">
				</div>
			</div>
		</div>
	</div>
</div>

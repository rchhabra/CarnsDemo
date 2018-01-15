<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<spring:url value="/my-account/order-approval-dashboard" var="orderApprovalDashboardUrl" htmlEscape="false" />
<div class="row heading-with-button">
	<div class="col-xs-7 col-sm-8">
		<h2 class="h2">
			<spring:theme code="text.account.orderApprovalDetails.label" />
		</h2>
	</div>
	<div class="col-xs-5 col-sm-4 button-inline">
		<a href="${orderApprovalDashboardUrl}">
			<button type="button" class="btn btn-primary btn-block">
				<spring:theme code="text.account.orderApprovalDetails.back.button" />
			</button>
		</a>
	</div>
</div>
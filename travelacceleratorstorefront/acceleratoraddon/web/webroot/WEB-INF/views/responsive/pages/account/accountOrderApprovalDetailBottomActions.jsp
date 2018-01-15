<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="b2b-order" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/order"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div class="row">
	<spring:url value="/my-account/order/approvalDecision" var="orderApprovalDecisionURL" htmlEscape="false" />
	<b2b-order:orderApprovalDecisionPopup orderApprovalDecisionForm="${orderApprovalDecisionForm}" orderApprovalData="${orderApprovalData}" orderApprovalDecisionURL="${orderApprovalDecisionURL}" decision="APPROVE" actionButtonLabel="text.account.orderApproval.approveButton.displayName"
		commentLabel="text.account.orderApproval.approve.reason" />
	<b2b-order:rejectOrderAction />
</div>

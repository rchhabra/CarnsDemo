<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="b2b-order" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/order"%>
<spring:url value="/my-account/order/approvalDecision" var="orderApprovalDecisionURL" htmlEscape="false" />
<b2b-order:orderApprovalDecisionPopup orderApprovalDecisionForm="${orderApprovalDecisionForm}" orderApprovalData="${orderApprovalData}" orderApprovalDecisionURL="${orderApprovalDecisionURL}" decision="REJECT" actionButtonLabel="text.account.orderApproval.rejectButton.displayName"
	commentLabel="text.account.orderApproval.reject.reason" />

<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="b2b-order" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/order"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%-- The Approve/ Reject Button --%>
<div class="clearfix">
	<div class="disable-link">
		<spring:url value="/my-account/order/approvalDecision" var="orderApprovalDecisionURL" htmlEscape="false" />
		<b2b-order:orderApprovalDecisionPopup orderApprovalDecisionForm="${orderApprovalDecisionForm}" orderApprovalData="${orderApprovalData}" orderApprovalDecisionURL="${orderApprovalDecisionURL}" decision="APPROVE" actionButtonLabel="text.account.orderApproval.approveButton.displayName"
			commentLabel="text.account.orderApproval.approve.reason" />
		<b2b-order:rejectOrderAction />
	</div>
</div>
<%-- Customer Approvals --%>
<c:if test="${not empty orderApprovalData.orderHistoryEntriesData}">
	<div class="margin-bottom-25">
		<div class="customer-approval-comments">
			<b2b-order:orderHistoryEntriesDetailsItem orderHistoryEntries="${orderApprovalData.orderHistoryEntriesData}" itemHolderTitleKey="text.account.orderHistoryEntry.customerTitle" />
		</div>
	</div>
</c:if>
<%-- Merchant Approvals --%>
<c:if test="${not empty orderApprovalData.merchantApprovalHistoryEntriesData}">
	<div class="margin-bottom-25">
		<b2b-order:orderHistoryEntriesDetailsItem orderHistoryEntries="${orderApprovalData.merchantApprovalHistoryEntriesData}" itemHolderTitleKey="text.account.orderHistoryEntry.merchantTitle" />
	</div>
</c:if>

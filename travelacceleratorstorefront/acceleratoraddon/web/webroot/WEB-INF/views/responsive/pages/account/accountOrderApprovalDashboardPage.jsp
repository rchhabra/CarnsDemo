<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/nav"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<spring:url value="/my-account/orderApprovalDetails/" var="orderApprovalDetailsUrl" />
<div class="panel-heading">
    <h3 class="title title-collapse">
    	<spring:theme code="text.account.orderApprovalDashboard" />
    </h3>
    <button data-toggle="collapse" aria-expanded="true" aria-controls="orderApproval" data-target="#orderApproval" class="show-hide-button">
        <spring:theme code="text.account.orderApprovalDashBoard.button.collapseExpand" />
    </button>
</div>
<c:set var="searchUrl" value="/my-account/approval-dashboard?sort=${ycommerce:encodeUrl(searchPageData.pagination.sort)}" />
<div class="panel-body collapse in" id="orderApproval">
	<c:choose>
		<c:when test="${empty searchPageData.results}">
			<div class="account-section-content	col-md-6 col-md-push-3 content-empty">
				<ycommerce:testId code="orderHistory_noOrders_label">
					<spring:theme code="text.account.orderHistory.noOrders" />
				</ycommerce:testId>
			</div>
		</c:when>
		<c:otherwise>
			<div class="panel-body">
				<div class="account-orderhistory-pagination">
					<nav:pagination top="true" msgKey="text.account.orderHistory.page" showCurrentPageInfo="true" hideRefineButton="true" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchUrl}" numberPagesShown="${numberPagesShown}" />
				</div>
				<div class="account-overview-table">
					<div class="responsive-table">
						<table class="table">
							<tbody>
								<tr class="account-orderhistory-table-head responsive-table-head hidden-xs inner-table-head">
									<th><spring:theme code="text.account.orderApprovalDashBoard.orderNumber" /></th>
									<th><spring:theme code="text.account.orderApprovalDashBoard.orderStatus" /></th>
									<th><spring:theme code="text.account.orderApprovalDetails.orderPlacedBy" /></th>
									<th><spring:theme code="text.account.orderHistory.datePlaced" /></th>
									<th><spring:theme code="text.account.order.total" /></th>
								</tr>
								<c:forEach items="${searchPageData.results}" var="order">
									<tr class="responsive-table-item">
										<td class="hidden-sm hidden-md hidden-lg"><spring:theme code='text.account.orderApprovalDashBoard.orderNumber' /></td>
										<td class="responsive-table-cell"><ycommerce:testId code="orderApprovalDashboard_orderNumber_link">
												<a href="${orderApprovalDetailsUrl}${ycommerce:encodeUrl(order.workflowActionModelCode)}">${fn:escapeXml(order.b2bOrderData.code)}</a>
											</ycommerce:testId></td>
										<td class="hidden-sm hidden-md hidden-lg"><spring:theme code='text.account.orderApprovalDashBoard.orderStatus' /></td>
										<td class="responsive-table-cell"><ycommerce:testId code="orderApprovalDashboard_orderStatus_label">
												<spring:theme code="text.account.order.status.display.${order.b2bOrderData.statusDisplay}" />
											</ycommerce:testId></td>
										<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.orderApprovalDetails.orderPlacedBy" /></td>
										<td class="responsive-table-cell"><ycommerce:testId code="orderApprovalDashboard_orderpurchaser_link">
										${fn:escapeXml(order.b2bOrderData.b2bCustomerData.name)}
									</ycommerce:testId></td>
										<td class="hidden-sm hidden-md hidden-lg"><spring:theme code='text.account.orderHistory.datePlaced' /></td>
										<td class="responsive-table-cell"><fmt:formatDate value="${order.b2bOrderData.created}" dateStyle="medium" timeStyle="short" type="both" /></td>
										<td class="hidden-sm hidden-md hidden-lg"><spring:theme code='text.account.order.total' /></td>
										<td class="responsive-table-cell">${fn:escapeXml(order.b2bOrderData.totalPrice.formattedValue)}</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</c:otherwise>
	</c:choose>
</div>
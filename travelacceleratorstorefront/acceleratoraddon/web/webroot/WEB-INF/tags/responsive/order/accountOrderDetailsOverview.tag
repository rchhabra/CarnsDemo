<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="col-xs-12">
	<div class="row">
		<div class="col-xs-12 well well-lg well-tertiary">
			<div class="col-sm-12 col-no-padding">
				<div class="col-sm-4 item-wrapper">
					<div class="item-group">
						<ycommerce:testId code="orderDetail_overviewOrderID_label">
							<span class="item-label">
								<spring:theme code="text.account.orderHistory.orderNumber" />
							</span>
							<span class="item-value">${fn:escapeXml(orderData.code)}</span>
						</ycommerce:testId>
					</div>
					<div class="item-group">
						<ycommerce:testId code="orderDetail_overviewOrderStatus_label">
							<span class="item-label">
								<spring:theme code="text.account.orderHistory.orderStatus" />
							</span>
							<c:if test="${not empty orderData.statusDisplay}">
								<span class="item-value">
									<spring:theme code="text.account.order.status.display.${fn:escapeXml(orderData.statusDisplay)}" />
								</span>
							</c:if>
						</ycommerce:testId>
					</div>
				</div>
				<div class="col-sm-4 item-wrapper">
					<div class="item-group">
						<ycommerce:testId code="orderDetail_overviewStatusDate_label">
							<span class="item-label">
								<spring:theme code="text.account.orderHistory.datePlaced" />
							</span>
							<span class="item-value">
								<fmt:formatDate value="${order.created}" dateStyle="medium" timeStyle="short" type="both" />
							</span>
						</ycommerce:testId>
					</div>
					<div class="item-group">
						<ycommerce:testId code="orderDetail_overviewPlacedBy_label">
							<span class="item-label">
								<spring:theme code="checkout.multi.summary.orderPlacedBy" />
							</span>
							<span class="item-value">
								<spring:theme code="text.company.user.${fn:escapeXml(order.b2bCustomerData.titleCode)}.name" text="" />
								&nbsp;${fn:escapeXml(fn:escapeXml(order.b2bCustomerData.firstName))}&nbsp;${fn:escapeXml(fn:escapeXml(order.b2bCustomerData.lastName))}
							</span>
						</ycommerce:testId>
					</div>
					<div class="item-group">
						<c:if test="${orderData.paymentType.code=='ACCOUNT'}">
							<ycommerce:testId code="orderDetail_overviewParentBusinessUnit_label">
								<span class="item-label">
									<spring:theme code="text.account.order.orderDetails.ParentBusinessUnit" />
								</span>
								<span class="item-value">${fn:escapeXml(orderData.costCenter.unit.name)}</span>
							</ycommerce:testId>
						</c:if>
					</div>
				</div>
				<div class="col-sm-4 item-wrapper">
					<div class="item-group">
						<c:if test="${orderData.paymentType.code=='ACCOUNT'}">
							<ycommerce:testId code="orderDetail_overviewCostCenter_label">
								<span class="item-label">
									<spring:theme code="text.account.order.orderDetails.CostCenter" />
								</span>
								<span class="item-value">${fn:escapeXml(orderData.costCenter.name)}</span>
							</ycommerce:testId>
						</c:if>
					</div>
					<div class="item-group">
						<ycommerce:testId code="orderDetail_overviewOrderTotal_label">
							<span class="item-label">
								<spring:theme code="text.account.order.total" />
							</span>
							<span class="item-value">
								<format:price priceData="${order.totalPrice}" />
							</span>
						</ycommerce:testId>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/responsive/order"%>
<%@ taglib prefix="b2b-order" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/order"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:if test="${not empty orderData}">
	<div class="panel panel-primary panel-list account-list">
		<div class="panel-heading">
			<h3 class="title">
				<spring:theme code="text.account.order.orderDetails.billingInformtion" />
			</h3>
		</div>
		<ycommerce:testId code="orderDetails_paymentDetails_section">
			<div class="panel-body">
				<div class="panel panel-default panel-spaced ">
					<div class="panel-heading">
						<h3 class="panel-title">
							<spring:theme code="text.account.paymentDetails" />
						</h3>
					</div>
					<div class="panel-body">
						<fieldset class="fieldset">
							<c:if test="${orderData.paymentType.code eq 'CARD'}">
								<div class="col-sm-6 order-billing-address">
									<order:billingAddressItem order="${orderData}" />
								</div>
								<c:if test="${not empty orderData.paymentInfo}">
									<div class="col-sm-6 order-payment-data">
										<order:paymentDetailsItem order="${orderData}" />
									</div>
								</c:if>
							</c:if>
							<c:if test="${orderData.paymentType.code eq 'ACCOUNT'}">
								<div class="col-xs-12 col-sm-12">
									<b2b-order:paymentDetailsAccountItem order="${orderData}" />
								</div>
							</c:if>
						</fieldset>
					</div>
				</div>
			</div>
		</ycommerce:testId>
	</div>
</c:if>
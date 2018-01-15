<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/template"%>
<%@ taglib prefix="reservation" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/reservation"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/addons/travelcheckout/responsive/address"%>
<%@ taglib prefix="voucher" tagdir="/WEB-INF/tags/addons/travelcheckout/responsive/voucher"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="account" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/account"%>
<%@ taglib prefix="progress" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/progress"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:url value="${currentStepUrl}" var="choosePaymentMethodUrl" />
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
	<c:choose>
		<c:when test="${bookingJourney=='BOOKING_PACKAGE'}">
			<progress:travelBookingProgressBar stage="payment" amend="${amend}" bookingJourney="${bookingJourney}" />
		</c:when>
		<c:otherwise>
			<progress:bookingProgressBar stage="payment" amend="${amend}" bookingJourney="${bookingJourney}" />
		</c:otherwise>
	</c:choose>
	<div class="container payment-details">
		<div class="margin-reset clearfix">
			<c:if test="${not empty paymentFormUrl}">
				<div class="col-xs-12">
					<h2 class="h2">
						<spring:theme code="checkout.summary.paymentMethod.enterDetails.header" text="Enter your Payment Details" />
					</h2>
				</div>
				<div class="col-xs-12 col-sm-9 payment-wrap y_nonItineraryContentArea">
					<cms:pageSlot position="PaymentOptions" var="feature">
						<cms:component component="${feature}" element="section" />
					</cms:pageSlot>
					<c:if test="${not empty paymentInfos}">
						<div class="panel panel-primary panel-list">
							<div class="panel-heading">
								<h3 class="title">
									<spring:theme code="checkout.summary.paymentMethod.savedPayments.header" text="Saved Payment Options" />
								</h3>
							</div>
							<div class="panel-body" id="saved-payment">
								<c:forEach items="${paymentInfos}" var="paymentInfo" varStatus="status">
									<div class="panel panel-default my-account-secondary-panel">
										<div class="panel-heading">
											<h3 class="panel-title">Card ${fn:escapeXml(status.count)}</h3>
										</div>
										<div class="panel-body option">
											<div class="fieldset">
												<div class="row">
													<account:paymentInfo paymentInfo="${paymentInfo}" />
													<div class="col-xs-12 col-md-6">
														<div class="form-group col-xs-6 col-sm-6 col-md-6 bottom-align-parent">
															<form:form action="${fn:escapeXml(request.contextPath)}/checkout/multi/payment-method/choose" method="GET">
																<input type="hidden" name="selectedPaymentMethodId" value="${fn:escapeXml(paymentInfo.id)}" />
																<button type="submit" class="btn btn-primary btn-block bottom-align" tabindex="${fn:escapeXml(status.count + 21)}">
																	<spring:theme code="checkout.multi.sop.useToPay" text="Select" />
																</button>
															</form:form>
														</div>
														<div class="form-group col-xs-6 col-sm-6 col-md-6 bottom-align-parent">
															<form:form action="${fn:escapeXml(request.contextPath)}/checkout/multi/payment-method/remove" method="POST">
																<input type="hidden" name="paymentInfoId" value="${fn:escapeXml(paymentInfo.id)}" />
																<button type="submit" class="btn btn-default btn-block bottom-align" tabindex="${fn:escapeXml(status.count + 22)}">
																	<spring:theme code="checkout.multi.sop.remove" text="Remove" />
																</button>
															</form:form>
														</div>
													</div>
												</div>
											</div>
										</div>
									</div>
								</c:forEach>
							</div>
						</div>
					</c:if>
					<form:form id="silentOrderPostForm" name="silentOrderPostForm" commandName="sopPaymentDetailsForm" class="create_update_payment_form" action="${paymentFormUrl}" method="POST">
						<ycommerce:testId code="paymentDetailsForm">
							<input type="hidden" name="orderPage_receiptResponseURL" value="${silentOrderPageData.parameters['orderPage_receiptResponseURL']}" />
							<input type="hidden" name="orderPage_declineResponseURL" value="${silentOrderPageData.parameters['orderPage_declineResponseURL']}" />
							<input type="hidden" name="orderPage_cancelResponseURL" value="${silentOrderPageData.parameters['orderPage_cancelResponseURL']}" />
							<c:forEach items="${sopPaymentDetailsForm.signatureParams}" var="entry" varStatus="status">
								<input type="hidden" id="${fn:escapeXml(entry.key)}" name="${fn:escapeXml(entry.key)}" value="${fn:escapeXml(entry.value)}" />
							</c:forEach>
							<c:forEach items="${sopPaymentDetailsForm.subscriptionSignatureParams}" var="entry" varStatus="status">
								<input type="hidden" id="${fn:escapeXml(entry.key)}" name="${fn:escapeXml(entry.key)}" value="${fn:escapeXml(entry.value)}" />
							</c:forEach>
							<%-- Payment Details --%>
							<div class="panel panel-primary panel-list">
								<div class="panel-heading">
									<h3 class="title">
										<spring:theme code="checkout.summary.paymentMethod.payment.header" text="New Payment Method" />
									</h3>
								</div>
								<div class="panel-body">
									<fieldset class="fieldset">
										<div class="row">
											<div class="form-group col-sm-6">
												<formElement:formSelectBox selectCSSClass="form-control" idKey="pd-cardtype" labelKey="payment.cardType" path="card_cardType" mandatory="true" skipBlank="false" skipBlankMessageKey="payment.cardType.pleaseSelect" items="${sopCardTypes}" tabindex="1" />
											</div>
											<div class="form-group col-sm-6">
												<formElement:formInputBox idKey="pd-card-number" labelKey="payment.cardNumber" path="card_accountNumber" inputCSS="y_cardNumber" mandatory="true" tabindex="2" autocomplete="off" />
											</div>
										</div>
										<%-- / .row --%>
										<div class="row">
											<div class="col-sm-12">
												<div class="row">
													<div class="form-group col-sm-3 nowrap">
														<formElement:formSelectBox selectCSSClass="form-control" idKey="pd-valid-day" labelKey="valid.from.payment.month" path="card_startMonth" mandatory="true" skipBlank="false" skipBlankMessageKey="payment.month" items="${months}" tabindex="3" />
													</div>
													<div class="form-group col-sm-3">
														<formElement:formSelectBox selectCSSClass="form-control" idKey="pd-valid-year" labelKey="valid.from.payment.year" labelCSS="invisible nowrap" path="card_startYear" mandatory="true" skipBlank="false" skipBlankMessageKey="payment.year" items="${startYears}" tabindex="4" />
													</div>
													<div class="form-group col-sm-3 nowrap">
														<formElement:formSelectBox selectCSSClass="form-control" idKey="pd-exp-day" labelKey="expiry.payment.month" path="card_expirationMonth" mandatory="true" skipBlank="false" skipBlankMessageKey="payment.month" items="${months}" tabindex="5" />
													</div>
													<div class="form-group col-sm-3">
														<formElement:formSelectBox selectCSSClass="form-control" idKey="pd-exp-year" labelKey="expiry.payment.year" labelCSS="invisible nowrap" path="card_expirationYear" mandatory="true" skipBlank="false" skipBlankMessageKey="payment.year" items="${expiryYears}" tabindex="6" />
													</div>
												</div>
											</div>
										</div>
										<%-- / .row --%>
										<div class="row">
											<div class="form-group col-sm-6">
												<formElement:formInputBox idKey="pd-card-name" labelKey="payment.nameOnCard" path="card_nameOnCard" inputCSS="y_nameOnCard" tabindex="7" mandatory="true" />
											</div>
										</div>
										<div class="row">
											<div class="col-sm-3">
												<formElement:formInputBox idKey="pd-card-verification" labelKey="payment.cvn" path="card_cvNumber" inputCSS="y_cardVerification" mandatory="true" tabindex="8" />
											</div>
										</div>
										<%-- / .row --%>
										<div class="row">
											<div class="clearfix">
												<label for="dis-assistance" class="col-xs-11 col-sm-3">
													<sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')">
														<formElement:formCheckbox idKey="savePaymentInfo1" labelKey="checkout.multi.sop.savePaymentInfo" path="savePaymentInfo" inputCSS="" labelCSS="" mandatory="false" tabindex="9" />
													</sec:authorize>
												</label>
											</div>
										</div>
										<%-- / .row --%>
									</fieldset>
								</div>
							</div>
							<input type="hidden" value="${fn:escapeXml(silentOrderPageData.parameters['billTo_email'])}" class="text" name="billTo_email" id="billTo_email">
							<div class="panel panel-primary panel-list">
								<div class="panel-heading">
									<h3 class="title">
										<spring:theme code="checkout.summary.paymentMethod.billing.address.header" text="Billing Address" />
									</h3>
								</div>
								<div class="panel-body">
									<fieldset class="fieldset">
										<address:billAddressFormSelector supportedCountries="${countries}" regions="${regions}" tabindex="10" />
									</fieldset>
								</div>
							</div>
							<fieldset>
								<div class="row">
									<div class="form-group col-xs-12 col-sm-6 col-md-3 pull-right">
										<button id="y_selectPaymentMethod" class="btn y btn-block bottom-align">
											<spring:theme code="checkout.summary.paymentMethod.next.button" text="Next" />
										</button>
									</div>
								</div>
								<%-- / .row --%>
							</fieldset>
						</ycommerce:testId>
					</form:form>
				</div>
				<div class="col-xs-12 col-sm-3">
					<aside id="sidebar" class="reservation y_reservationSideBar">
						<div class="main-wrap">
							<cms:pageSlot position="Reservation" var="feature" element="div">
								<cms:component component="${feature}" />
							</cms:pageSlot>
						</div>
						<div class="promotions hidden-xs">
							<cms:pageSlot position="SideContent" var="feature" element="section">
								<cms:component component="${feature}" />
							</cms:pageSlot>
						</div>
					</aside>
				</div>
			</c:if>
		</div>
	</div>
	<reservation:fullReservationOverlay />
</template:page>

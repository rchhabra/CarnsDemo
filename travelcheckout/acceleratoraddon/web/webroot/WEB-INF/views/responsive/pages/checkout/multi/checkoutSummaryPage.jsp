<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/addons/travelcheckout/responsive/checkout/multi"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="progress" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/progress"%>
<%@ taglib prefix="booking" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/booking"%>
<spring:url value="/checkout/multi/summary/placeOrder" var="placeOrderUrl" />
<spring:url value="/checkout/multi/termsAndConditions" var="getTermsAndConditionsUrl" />
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
	<c:choose>
		<c:when test="${bookingJourney=='BOOKING_PACKAGE'}">
			<progress:travelBookingProgressBar stage="payment" amend="${amend}" bookingJourney="${bookingJourney}" />
		</c:when>
		<c:otherwise>
			<progress:bookingProgressBar stage="payment" amend="${amend}" bookingJourney="${bookingJourney}" />
		</c:otherwise>
	</c:choose>
	<div class="container y_checkoutSummary">
		<div class="col-sm-offset-2 col-sm-10 col-xs-12">
			<div class="row">
				<h2 class="h2">
					<spring:theme code="checkout.multi.booking.summary.header" text="Booking Summary" />
				</h2>
			</div>
		</div>
		<div class="margin-reset clearfix">
			<div class="col-sm-offset-2 col-sm-10">
				<div class="col-xs-12 col-sm-9">
					<div class="row payment-wrap">
						<multi-checkout:checkoutSteps checkoutSteps="${checkoutSteps}" progressBarId="${progressBarId}">
							<ycommerce:testId code="checkoutStepFour">
								<div class="panel panel-primary panel-list">
									<div class="panel-heading">
										<h3 class="title">
											<spring:theme code="checkout.summary.review.title" text="Final Review" />
										</h3>
									</div>
									<%-- ADD MARGIN/PADDING TOP --%>
									<div class="">
										<cms:pageSlot position="CenterContent" var="feature" element="div">
											<cms:component component="${feature}" />
										</cms:pageSlot>
									</div>
									<multi-checkout:cartTotal />
									<multi-checkout:payingAmount />
								</div>
								<form:form action="${placeOrderUrl}" id="placeOrderForm1" commandName="placeOrderForm">
									<div class="panel panel-primary panel-list">
										<div class="panel-heading">
											<h3 class="panel-title">
												<spring:theme code="checkout.summary.terms.title" text="Terms and Conditions" />
											</h3>
										</div>
										<div class="panel-body">
											<fieldset class="fieldset">
												<div class="row">
													<div class="col-sm-12">
														<p>
															<spring:theme code="checkout.summary.terms.conditions.text" />
														</p>
													</div>
													<div class="col-xs-12 text-right">
														<div class="row">
															<label for="Terms1">
																<spring:theme code="checkout.summary.terms.accept" text="Terms and Conditions" />
															</label>
															<form:checkbox id="Terms1" path="termsCheck" />
														</div>
													</div>
												</div>
											</fieldset>
										</div>
									</div>
									<fieldset>
										<div class="row">
											<div class="form-group col-xs-12 col-sm-6 col-md-3 pull-right">
												<button id="placeOrder" type="submit" class="btn y btn-block bottom-align y_payNow" disabled="disabled">
													<spring:theme code="checkout.summary.pay.now.button" text="Pay Now" />
												</button>
											</div>
										</div>
									</fieldset>
								</form:form>
							</ycommerce:testId>
						</multi-checkout:checkoutSteps>
					</div>
				</div>
			</div>
		</div>
	</div>
</template:page>

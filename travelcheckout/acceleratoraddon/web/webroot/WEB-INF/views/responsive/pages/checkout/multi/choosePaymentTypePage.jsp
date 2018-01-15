<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/addons/travelcheckout/responsive/checkout/multi"%>
<%@ taglib prefix="reservation" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/reservation"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
	<div class="container">
		<div class="col-xs-12 row">
			<h2 class="h2">
				<spring:theme code="checkout.multi.secure.checkout" />
			</h2>
		</div>
		<div class="row">
			<div class="col-sm-9 col-xs-12 y_nonItineraryContentArea">
				<ycommerce:testId code="checkoutSteps">
					<div class="checkout-steps">
						<div class="step-body">
							<multi-checkout:paymentTypeForm />
							<div class="panel panel-primary panel-list">
								<div class="panel-heading">
									<h3 class="title">
										<spring:theme code="checkout.summary.paymentMethod.payment.header" text="Payment Method" />
									</h3>
								</div>
							</div>
							<div class="panel panel-primary panel-list">
								<div class="panel-heading">
									<h3 class="title">
										<spring:theme code="checkout.summary.paymentMethod.billing.address.header" text="Billing Address" />
									</h3>
								</div>
							</div>
							<div class="panel panel-primary panel-list">
								<div class="panel-heading">
									<h3 class="title">
										<spring:theme code="voucher.header.title" text="Vouchers" />
									</h3>
								</div>
							</div>
						</div>
					</div>
				</ycommerce:testId>
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
		</div>
	</div>
	<reservation:fullReservationOverlay />
</template:page>

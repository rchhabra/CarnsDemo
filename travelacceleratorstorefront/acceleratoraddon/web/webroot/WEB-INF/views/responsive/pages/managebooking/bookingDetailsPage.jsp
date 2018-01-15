<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/format"%>
<%@ taglib prefix="booking" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/booking"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<template:page pageTitle="${pageTitle}">
	<div class="container">
		<div class="row">
			<div class="col-xs-12 col-sm-offset-3 col-sm-9">
				<booking:amendBookingMessage amendBookingRefundAmount="${amendBookingRefundAmount}" amendBookingResult="${amendBookingResult}" amendBookingErrorResult="${amendBookingErrorResult}" isAccommodationAvailable="${isAccommodationAvailable}" />
				<booking:cancelResultMessage refundResult="${refundResult}" cancellationResult="${cancellationResult}" refundedAmount="${refundedAmount}" cancellationParameter="${cancellationParameter}" />
			</div>
		</div>
		<div class="row heading-with-button">
			<div class="col-xs-12 col-sm-offset-3 col-sm-5">
				<h2 class="h2">
					<spring:theme code="text.page.managemybooking.bookingdetails" text="Booking Details" />
				</h2>
			</div>
			<sec:authorize access="hasAnyRole('ROLE_CUSTOMERGROUP')">
				<div class="col-xs-12 col-sm-4 button-inline">
					<a class="btn btn-primary btn-block" href="<c:url value="/my-account/profile"/>">
						<spring:theme code="text.page.managemybooking.backtoprofile" text="Back to my profile" />
					</a>
				</div>
			</sec:authorize>
		</div>
		<div class="margin-reset clearfix">
			<div class="col-xs-12 col-sm-3">
				<aside id="sidebar">
					<div class="promotions hidden-xs">
						<cms:pageSlot position="LeftContent" var="feature">
							<cms:component component="${feature}" element="section" />
						</cms:pageSlot>
					</div>
				</aside>
			</div>
			<div class="col-xs-12 col-sm-9">				
				<div class="panel panel-primary panel-list booking room-details clearfix">
					<div class="panel-heading">
						<h3 class="title">
							<spring:theme code="text.page.managemybooking.bookingreferece" arguments="${bookingReference}" />
						</h3>
					</div>
					<cms:pageSlot position="RightContent" var="feature">
						<cms:component component="${feature}"/>
					</cms:pageSlot>
				</div>
				<div class="row">
					<div class="col-xs-12 col-sm-4 pull-right form-group">
						<a class="btn btn-primary btn-block" href="<c:url value="/my-account/profile"/>">
							<spring:theme code="text.page.managemybooking.backtoprofile" text="Back to my profile" />
						</a>
					</div>
				</div>
			</div>
		</div>
	</div>
</template:page>

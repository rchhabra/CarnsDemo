<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="progress" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/progress"%>
<%@ taglib prefix="booking" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/booking"%>
<%@ taglib prefix="bookingDetails" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/bookingdetails"%>
<c:set var="isValidCustomerGroup" value="false" />
<spring:htmlEscape defaultHtmlEscape="true" />
<template:page pageTitle="${pageTitle}">
	<c:choose>
		<c:when test="${bookingJourney=='BOOKING_PACKAGE'}">
			<progress:travelBookingProgressBar stage="confirmation" amend="${amend}" bookingJourney="${bookingJourney}" />
		</c:when>
		<c:otherwise>
			<progress:bookingProgressBar stage="confirmation" amend="${amend}" bookingJourney="${bookingJourney}" />
		</c:otherwise>
	</c:choose>
	<div class="container">
		<div class="col-xs-12 col-sm-3 side-to-top">
			<aside id="sidebar">
				<div class="promotions hidden-xs">
					<section>
						<cms:pageSlot position="LeftContent" var="feature">
							<cms:component component="${feature}" element="section" />
						</cms:pageSlot>
					</section>
				</div>
			</aside>
		</div>
		<div class="col-xs-12 col-sm-9">
			<div class="row heading-with-button">
				<div class="col-xs-12 col-sm-8">
					<h2 class="h2">
						<spring:theme code="booking.confirmation.header" text="Booking Confirmation" />
					</h2>
				</div>
				<div class="col-xs-12 col-sm-4 button-inline">
					<sec:authorize access="hasAnyRole('ROLE_CUSTOMERGROUP')">
						<c:set var="isValidCustomerGroup" value="true" />
					</sec:authorize>
					<c:if test="${isValidCustomerGroup}">
						<c:url value="/manage-booking/booking-details/${bookingReference}" var="manageBookingUrl" />
						<a class="btn btn-primary btn-block" href="${manageBookingUrl}">
							<spring:theme code="booking.confirmation.manage.booking.button" text="Manage Booking" />
						</a>
					</c:if>
				</div>
			</div>
			<%-- My Saved Payment --%>
			<div class="panel panel-primary panel-list my-account-panels">
				<div class="panel-heading">
					<h3 class="title">
						<spring:theme code="booking.confirmation.details.header" text="Booking Details" />
					</h3>
				</div>
				<div id="booking-details-body" class="panel-body">
					<div class="row">
						<div class="panel button-wrap">
							<div class="col-xs-12 col-md-4">
								<dl class="booking status">
									<dt>
										<spring:theme code="booking.confirmation.reference.number.header" text="Booking Reference:" />
									</dt>
									<dd>${bookingReference}</dd>
								</dl>
							</div>
						</div>
					</div>
					<%-- Here show components --%>
					<cms:pageSlot position="RightContent" var="feature">
						<cms:component component="${feature}" element="section" />
					</cms:pageSlot>
					<%-- Stop components area --%>
				</div>
				<%-- / .panel-body (outer) --%>
			</div>
			<%-- / .panel (outer) --%>
			<c:if test="${isValidCustomerGroup}">
				<div class="row bottom-row">
					<div class="col-xs-12 col-sm-4 col-sm-offset-8">
						<a class="btn btn-primary btn-block" href="${manageBookingUrl}">
							<spring:theme code="booking.confirmation.manage.booking.button" text="Manage Booking" />
						</a>
					</div>
				</div>
			</c:if>
			<%-- / My Saved Payment --%>
		</div>
	</div>
</template:page>

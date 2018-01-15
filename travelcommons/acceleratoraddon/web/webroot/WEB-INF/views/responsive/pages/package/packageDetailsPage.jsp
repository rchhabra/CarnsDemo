<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/template"%>
<%@ taglib prefix="packageDetails" tagdir="/WEB-INF/tags/addons/travelcommons/responsive/packagedetails"%>
<%@ taglib prefix="accommodationDetails" tagdir="/WEB-INF/tags/addons/accommodationaddon/responsive/accommodationdetails"%>
<%@ taglib prefix="progress" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/progress"%>
<%@ taglib prefix="reservation" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/reservation"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:url var="nextUrl" value="${nextURL}" />
<template:page pageTitle="${pageTitle}">
	<input type="hidden" id="y_amendPackageDetailsPage" value="${fn:escapeXml(amend)}" />
	<progress:travelBookingProgressBar stage="packages" amend="${amend}" bookingJourney="${bookingJourney}" />
	<c:set var="accommodationAvailabilityResponse" value="${packageAvailabilityResponse.accommodationPackageResponse.accommodationAvailabilityResponse}" />
	<c:set var="property" value="${packageAvailabilityResponse.accommodationPackageResponse.accommodationAvailabilityResponse.accommodationReference}" />
	<div class="container">
		<div class="col-xs-12">
			<c:if test="${!isPackageUnavailable}">
				<packageDetails:packageDetailsChangedMessage />
			</c:if>
			<h2 class="h2">
				<spring:theme code="text.package.details.label.text" text="Package Details" />
			</h2>
		</div>
		<div class="margin-reset clearfix package-details-wrap">
			<div class="col-xs-12 col-sm-9 y_nonItineraryContentArea">
				<c:if test="${!amend}">
					<div class="modify-search panel panel-primary col-xs-12 col-sm-9">
						<div class="row">
							<cms:pageSlot position="LeftContent" var="feature" element="div">
								<cms:component component="${feature}" />
							</cms:pageSlot>
						</div>
					</div>
				</c:if>
				<div class="col-xs-12">
					<div class="row">
						<accommodationDetails:propertyDetails property="${property}" />
						<c:if test="${!isPackageUnavailable}">
							<c:if test="${not empty isPackageInCart && !isPackageInCart}">
								<packageDetails:addSelectedAccommodationForm accommodationAvailabilityResponse="${accommodationAvailabilityResponse}" addRoomToPackageUrl="${addRoomToPackageUrl}" />
							</c:if>
							<packageDetails:selectedRoomStays roomStays="${accommodationAvailabilityResponse.reservedRoomStays}" />
							<packageDetails:roomOptions accommodationAvailabilityResponse="${accommodationAvailabilityResponse}" />
							<c:choose>
								<c:when test="${amend and bookingJourney=='BOOKING_ACCOMMODATION_ONLY'}">
									<packageDetails:reservedTransportation transportPackageResponse="${packageAvailabilityResponse.transportPackageResponse}" />
								</c:when>
								<c:otherwise>
									<packageDetails:selectedTransportation transportPackageResponse="${packageAvailabilityResponse.transportPackageResponse}" />
									<packageDetails:transportationOptions transportPackageResponse="${packageAvailabilityResponse.transportPackageResponse}" />
								</c:otherwise>
							</c:choose>
							<accommodationDetails:propertyInformation property="${property}" />
							<div class="col-xs-12">
								<div class="row">
									<div class="panel panel-default my-account-primary-panel about-fare">
										<h2 class="panel-title h4">
											<a class="panel-heading panel-header-link collapsable collapsed" role="button" data-toggle="collapse" data-target=".about-fare .panel-body" aria-expanded="false" aria-controls="manage-booking">
												<spring:theme code="text.page.package.details.aboutfare.title" text="About Your Fare" />
											</a>
										</h2>
										<div class="panel-body collapse">
											<cms:pageSlot position="BottomContent" var="feature">
												<cms:component component="${feature}" />
											</cms:pageSlot>
										</div>
									</div>
								</div>
							</div>
						</c:if>
					</div>
				</div>
			</div>
				<%-- Begin Sidebar section --%>
			<div class="col-xs-12 col-sm-3">
				<aside id="sidebar" class="y_reservationSideBar reservation">
					<div class="main-wrap">
						<cms:pageSlot position="Reservation" var="feature" element="div">
							<cms:component component="${feature}" />
						</cms:pageSlot>
						<div class="row y_packageDetailsContinue">
							<div class="visible-xs-block col-xs-offset-1 col-xs-10">
								<a href="${nextUrl}" class="btn btn-secondary col-xs-12 y_packageDetailsContinueButton disabled">
									<spring:theme code="text.package.details.button.continue" text="Continue" />
								</a>
							</div>
						</div>
					</div>
					<div class="promotions hidden-xs">
						<cms:pageSlot position="SideContent" var="feature" element="section">
							<cms:component component="${feature}" />
						</cms:pageSlot>
					</div>
				</aside>
			</div>
		</div>
			<%-- End Sidebar section --%>
	</div>
	<div aria-hidden="false" role="dialog" tabindex="-1" id="package-build-modal" class="modal fade in">
		<div class="modal-dialog" role="document">
			<div class="alert alert-info" role="alert">
				<p class="y_packageProcessingContent">
					<spring:theme code="text.package.details.package.processing" text="Please wait while we're building your package..."/>
				</p>
			</div>
		</div>
	</div>
	<div class="y_continueBar continue-bar hidden-xs">
		<div class="container">
			<div class="row y_packageDetailsContinue">
				<div class="col-xs-offset-9 col-xs-3">
					<a href="${nextUrl}" class="btn btn-secondary col-xs-12 y_packageDetailsContinueButton disabled">
						<spring:theme code="text.package.details.button.continue" text="Continue" />
					</a>
				</div>
			</div>
		</div>
	</div>
	<reservation:fullReservationOverlay />
	<packageDetails:addPackageToCartErrorModal />
	<c:if test="${isPackageUnavailable}">
		<packageDetails:noPackageAvailabilityModal />
	</c:if>
</template:page>

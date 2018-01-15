<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/template"%>
<%@ taglib prefix="accommodationDetails" tagdir="/WEB-INF/tags/addons/accommodationaddon/responsive/accommodationdetails"%>
<%@ taglib prefix="progress" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/progress"%>
<%@ taglib prefix="reservation" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/reservation"%>
<c:url var="nextUrl" value="/accommodation-details/next" />
<spring:htmlEscape defaultHtmlEscape="true" />
<template:page pageTitle="${pageTitle}">
	<progress:bookingProgressBar stage="accommodation" amend="${amend}" bookingJourney="${bookingJourney}" />
	<div class="container">
		<div class="col-xs-12">
			<h2 class="h2">
				<spring:theme code="text.accommodation.listing.hotel.selection" />
			</h2>
		</div>
		<div class="margin-reset clearfix">
			<div class="col-xs-12 col-sm-9 y_nonItineraryContentArea">
				<accommodationDetails:propertyDetails property="${accommodationAvailabilityResponse.accommodationReference}" />
			<c:if test="${not empty bestCombinationAccommodationAvailabilityResponse}">
				<accommodationDetails:roomStayBestOptions />
			</c:if>
				<accommodationDetails:roomStays />
				<c:set var="property" value="${accommodationAvailabilityResponse.accommodationReference}" />
				<accommodationDetails:propertyInformation property="${property}" />
			</div>
			<%-- Begin Sidebar section --%>
			<div class="col-xs-12 col-sm-3">
				<aside id="sidebar" class="y_reservationSideBar reservation">
					<div class="main-wrap">
						<cms:pageSlot position="Reservation" var="feature" element="div">
							<cms:component component="${feature}" />
						</cms:pageSlot>
						<div class="row">
							<div class="visible-xs-block col-xs-offset-1 col-xs-10">
								<form class="y_continueForm" action="${nextUrl}" method="get">	
									<button class="btn btn-secondary col-xs-12 y_accommodationDetailsContinue" disabled>
										<spring:theme code="text.accommodation.details.button.continue" text="Continue" />
									</button>
								</form>
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
	<div class="y_continueBar continue-bar hidden-xs">
		<div class="container">
			<div class="row">
				<div class="col-xs-offset-9 col-xs-3">
					<form class="y_continueForm" action="${nextUrl}" method="get">	
						<button class="btn btn-secondary col-xs-12 y_accommodationDetailsContinue" disabled>
							<spring:theme code="text.accommodation.details.button.continue" text="Continue" />
						</button>
					</form>
				</div>
			</div>
		</div>
	</div>
	<reservation:fullReservationOverlay />
	<accommodationDetails:addAccommodationToCartErrorModal />
	<accommodationDetails:ratePlanRoomSelectStockLevelErrorModal />
	<accommodationDetails:validateCartErrorModal />
	<c:if test="${not empty isAccommodationAvailable && !isAccommodationAvailable}">
		<accommodationDetails:noAvailabilityModal />
	</c:if>
</template:page>

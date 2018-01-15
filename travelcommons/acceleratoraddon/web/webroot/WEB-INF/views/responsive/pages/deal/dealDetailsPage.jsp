<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/template"%>
<%@ taglib prefix="accommodationDetails" tagdir="/WEB-INF/tags/addons/accommodationaddon/responsive/accommodationdetails"%>
<%@ taglib prefix="progress" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/progress"%>
<%@ taglib prefix="reservation" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/reservation"%>
<%@ taglib prefix="packageDetails" tagdir="/WEB-INF/tags/addons/travelcommons/responsive/packagedetails"%>
<%@ taglib prefix="dealdetails" tagdir="/WEB-INF/tags/addons/travelcommons/responsive/dealdetails"%>
<%@ taglib prefix="deallisting" tagdir="/WEB-INF/tags/addons/travelcommons/responsive/deallisting"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:url var="nextUrl" value="/ancillary-extras" />
<template:page pageTitle="${pageTitle}">
	<progress:travelBookingProgressBar stage="packages" amend="${amend}" bookingJourney="${bookingJourney}" />
	<c:set var="accommodationAvailabilityResponse" value="${packageResponseData.accommodationPackageResponse.accommodationAvailabilityResponse}" />
	<c:set var="transportData" value="${packageResponseData.transportPackageResponse.fareSearchResponse}" />
	<c:forEach var="pricedItinerary" items="${transportData.pricedItineraries}">
		<c:if test="${pricedItinerary.originDestinationRefNumber == 0}">
			<c:set var="departureRoute" value="${pricedItinerary.itinerary.route}" />
			<c:if test="${fn:length(pricedItinerary.itinerary.originDestinationOptions[0].transportOfferings) gt 0}">
		    	<c:set var="originLocationCity" value="${pricedItinerary.itinerary.originDestinationOptions[0].transportOfferings[0].originLocationCity}" />
		    	<c:set var="destinationLocationCity" value="${pricedItinerary.itinerary.originDestinationOptions[0].transportOfferings[0].destinationLocationCity}" />
		    </c:if>
		</c:if>
	</c:forEach>
	<fmt:formatDate value="${accommodationAvailabilityResponse.roomStays[0].checkInDate}" pattern="dd/MM/yyyy" var="dealStartDate" />
	<fmt:formatDate value="${accommodationAvailabilityResponse.roomStays[0].checkOutDate}" pattern="dd/MM/yyyy" var="dealEndDate" />
	<div class="container">
	<div class="col-xs-12">
		<h2 class="h2">
			<spring:theme code="text.page.dealdetails.title" text="Deal Details" />
		</h2>
	</div>
	<div class="margin-reset clearfix package-details-wrap">
		<div class="col-xs-12 col-sm-9 y_nonItineraryContentArea">
			<%--To Add Update Deal Dates --%>
			<div id="y_dealUpdateDates">
				<dealdetails:dealUpdateDates dealBundleTemplateId="${dealBundleTemplateId}" displayForm="false"/>
			</div>
			<accommodationDetails:propertyDetails property="${accommodationAvailabilityResponse.accommodationReference}" />
			<packageDetails:selectedRoomStays roomStays="${accommodationAvailabilityResponse.roomStays}" />
			<div class="row form-group">
				<div class="col-xs-12 col-sm-offset-8 col-sm-offset-8 col-sm-4 y_packageSearch"  style="display: none;">
					<a href="" class="y_search_more_packages btn btn-primary col-xs-12" data-toggle="modal" data-target="#more-packages-modal">
						<spring:theme code="button.page.dealdetails.packages.showmore" text="Explore more packages" />
					</a>
					<deallisting:dealfinderattributes propertyData="${accommodationAvailabilityResponse}" transportData="${transportData}" returnRouteLocation="${departureRoute.destination}" departureRouteLocation="${departureRoute.origin}" departureLocationCity="${originLocationCity}" returnLocationCity="${destinationLocationCity}" departureDate="${dealStartDate}" returnDate="${dealEndDate}" />
				</div>
			</div>
			<div class="col-xs-12">
				<div class="row">
					<packageDetails:selectedTransportation transportPackageResponse="${packageResponseData.transportPackageResponse}" />
				</div>
			</div>
			<packageDetails:transportationOptions transportPackageResponse="${packageResponseData.transportPackageResponse}" />
			<accommodationDetails:propertyInformation property="${accommodationAvailabilityResponse.accommodationReference}" />
			<div class="col-xs-12">
				<div class="row">
					<div class="panel panel-default my-account-primary-panel about-fare">
						<h2 class="panel-title h4">
							<a class="panel-heading panel-header-link collapsable collapsed" role="button" data-toggle="collapse" data-target=".about-fare .panel-body" aria-expanded="false" aria-controls="manage-booking">
								<spring:theme code="text.page.dealdetails.aboutfare.title" text="About Your Fare" />
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
		</div>
		<div class="col-xs-12 col-sm-3">
			<aside id="sidebar" class="y_reservationSideBar reservation">
				<div class="main-wrap">
					<cms:pageSlot position="Reservation" var="feature" element="div">
						<cms:component component="${feature}" />
					</cms:pageSlot>
					<div class="row y_packageDetailsContinue">
						<div class="visible-xs-block col-xs-offset-1 col-xs-10">
							<a href="${nextUrl}" class="btn btn-secondary col-xs-12">
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
	<div class="y_continueBar continue-bar hidden-xs">
		<div class="container">
			<div class="row y_packageDetailsContinue">
				<div class="col-xs-offset-9 col-xs-3">
					<a href="${nextUrl}" class="btn btn-secondary col-xs-12">
						<spring:theme code="text.package.details.button.continue" text="Continue" />
					</a>
				</div>
			</div>
		</div>
	</div>
	<reservation:fullReservationOverlay />
	<packageDetails:addPackageToCartErrorModal />
	<deallisting:dealfinderform finderFormTitleCode="text.page.dealdetails.deal.finder.title.message" finderFormTitleText="Explore More Packages" />
</template:page>

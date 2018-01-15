<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/template"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ancillary" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/ancillary"%>
<%@ taglib prefix="travelcommonsancillary" tagdir="/WEB-INF/tags/addons/travelcommons/responsive/ancillary"%>
<%@ taglib prefix="progress" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/progress"%>
<%@ taglib prefix="reservation" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/reservation"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:url value="/cart/add" var="addToCartUrl" />
<c:url value="${nextURL}" var="nextPageUrl" />
<spring:htmlEscape defaultHtmlEscape="true" />
<template:page pageTitle="${pageTitle}">
	<progress:travelBookingProgressBar stage="extras" amend="${amend}" bookingJourney="${bookingJourney}" />
	<div class="container">
		<h2 class="h2">
			<spring:message code="text.ancillary.extras.offers.title" />
		</h2>
		<div class="margin-reset y_ancillarySection clearfix">
			<div class="col-xs-12 col-sm-9 y_nonItineraryContentArea">
				<c:if test="${not empty errorResult}">
					<div class="alert alert-danger alert-dismissible y_cancellationResult" role="alert">
						<button type="button" class="close" data-dismiss="alert" aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<p>
							<spring:message code="${errorResult}" />
						</p>
					</div>
				</c:if>
				<c:if test="${!amend or (amend and bookingJourney eq 'BOOKING_TRANSPORT_ONLY')}">
					<form:form id="addToCartForm" name="addToCartForm" class="y_addToCartForm" action="${addToCartUrl}" method="post">
						<input id="y_productCode" name="productCode" type="hidden" />
						<input id="y_quantity" name="qty" type="hidden" />
						<input id="y_transportOfferingCodes" name="transportOfferingCodes" type="hidden" />
						<input id="y_travelRouteCode" name="travelRouteCode" type="hidden" />
						<input id="y_travellerCode" name="travellerCode" type="hidden" />
						<input id="y_originDestinationRefNumber" name="originDestinationRefNumber" type="hidden" />
					</form:form>
					<c:if test="${not empty offerResponseData.offerGroups}">
						<c:forEach items="${offerResponseData.offerGroups}" var="offerGroup">
							<section class="panel panel-primary">
								<ancillary:offerItems offerGroup="${offerGroup}" itineraries="${offerResponseData.itineraries}" />
							</section>
						</c:forEach>
					</c:if>
					<c:if test="${!amend and !isDealInCart}">
						<section class="panel panel-primary ">
							<ancillary:upgradeBundle isPackage="${true}" />
						</section>
					</c:if>
					<section class="panel panel-primary y_ancillarySeatSection">
						<ancillary:seatingOptions itineraries="${offerResponseData.itineraries}" />
					</section>
				</c:if>
				<c:if test="${!amend or (amend and bookingJourney eq 'BOOKING_ACCOMMODATION_ONLY')}">
					<travelcommonsancillary:accommodationservices />
				</c:if>
			</div>
			<div class="col-xs-12 col-sm-3">
				<aside id="sidebar" class="reservation y_reservationSideBar">
					<div class="main-wrap">
						<cms:pageSlot position="Reservation" var="feature" element="div">
							<cms:component component="${feature}" />
						</cms:pageSlot>
						<div class="row">
							<div class="visible-xs-block col-xs-offset-1 col-xs-10">
								<a href="${nextPageUrl}" class="btn btn-secondary col-xs-12 ${!amend or (amend and bookingJourney eq 'BOOKING_TRANSPORT_ONLY') ? 'y_reservationContinueButton' : ''}" data-amend="${fn:escapeXml(originalOrderCode)}">
									<spring:message code="text.ancillary.button.continue" text="Continue" />
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
	</div>
	<div class="y_continueBar continue-bar hidden-xs">
		<div class="container">
			<div class="row">
				<div class="col-xs-offset-9 col-xs-3">
					<a href="${nextPageUrl}" class="btn btn-secondary col-xs-12 ${!amend or (amend and bookingJourney eq 'BOOKING_TRANSPORT_ONLY') ? 'y_reservationContinueButton' : ''}" data-amend="${fn:escapeXml(originalOrderCode)}">
						<spring:message code="text.ancillary.button.continue" text="Continue" />
					</a>
				</div>
			</div>
		</div>
	</div>
	<reservation:fullReservationOverlay />
	<ancillary:addProductToCartErrorModal />
	<ancillary:travelRestrictionModal />
	<ancillary:noUpgradeOptionAvailable/>
</template:page>

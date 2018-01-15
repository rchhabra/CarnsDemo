<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/template"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="accommodationsearch" tagdir="/WEB-INF/tags/addons/accommodationaddon/responsive/accommodationsearch"%>
<%@ taglib prefix="progress" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/progress"%>
<%@ taglib prefix="reservation" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/reservation"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<template:page pageTitle="${pageTitle}">
	<progress:bookingProgressBar stage="accommodation" bookingJourney="${bookingJourney}" />
	<div class="y_ListingPageParams" data-googleapi="${googleAPIKey}" data-resultViewType="${fn:escapeXml(resultsViewType)}"></div>
	<c:set var="resultsView" value="${fn:toUpperCase(resultsViewType)}" />
	<div class="container row-offcanvas row-offcanvas-left">
		<div class="col-xs-12">
			<h2 class="h2">
				<spring:theme code="accommodationsearch.title" text="Please choose your accommodation" />
			</h2>
		</div>
		<div class="accommodation-selection-wrap clearfix y_accommodationSelectionSection">
			<div class="col-xs-12 col-sm-9 y_nonItineraryContentArea">
				<div class="col-xs-12">
					<div class="row">
						<div class="modify-search panel panel-primary col-xs-12 col-sm-9">
							<div class="row">
								<cms:pageSlot position="LeftContent" var="feature" element="div">
									<cms:component component="${feature}" />
								</cms:pageSlot>
							</div>
						</div>
					</div>
				</div>
				<cms:pageSlot position="Refinement" var="feature">
					<cms:component component="${feature}" />
				</cms:pageSlot>
				<div class="clearfix accommodation-results-wrapper">
					<c:set var="urlParameters" value="${urlParameters}" />
					<c:set var="properties" value="${accommodationSearchResponse.properties}" />
					<div id="results-options" class="col-xs-12">
						<div class="col-xs-12 col-sm-4 col-sm-push-8 button-inline">
							<div class="row">
								<c:if test="${totalNumberOfResults gt 0 or not empty accommodationSearchResponse.criterion.facets or not empty accommodationSearchResponse.criterion.filteredFacets or not empty filterPropertyName}">
									<div class="col-xs-6 visible-xs-block">
										<button type="button" class="btn btn-default-light col-xs-12" data-toggle="offcanvas">
											<span class="glyphicon glyphicon-align-justify" aria-hidden="true"></span>
											<spring:theme code="text.accommodation.listing.filter" text="Filter" />
										</button>
									</div>
								</c:if>
								<c:if test="${totalNumberOfResults gt 0}">
									<div class="col-xs-6 ${resultsView == 'LISTVIEW' ? 'hidden' : ''}" id="y_displayListView">
										<button type="button" class="btn btn-default-light col-xs-12">
											<span class="glyphicon glyphicon-th-list" aria-hidden="true"></span>
											<spring:message code="text.accommodation.listing.result.view.list" text="List view" />
										</button>
									</div>
									<div class="col-xs-6 ${resultsView == 'GRIDVIEW' ? 'hidden' : ''}" id="y_displayGridView">
										<button type="button" class="btn btn-default-light col-xs-12 hidden-xs ">
											<span class="glyphicon glyphicon-th" aria-hidden="true"></span>
											<spring:message code="text.accommodation.listing.result.view.grid" text="Grid view" />
										</button>
									</div>
									<div class="col-xs-6 ${resultsView == 'MAPVIEW' ? 'hidden' : ''}" id="y_displayMapView">
										<button type="button" class="btn btn-default-light col-xs-12 ">
											<span class="glyphicon glyphicon-map-marker" aria-hidden="true"></span>
											<spring:message code="text.accommodation.listing.map.view.title" text="Map view" />
										</button>
									</div>
								</c:if>
							</div>
						</div>
						<c:if test="${totalNumberOfResults gt 0}">
							<div class="col-xs-12 col-sm-8 col-sm-pull-4 ${resultsView == 'MAPVIEW' ? 'hidden' : ''}" id="y_displaySortSelection">
								<div class="row">
									<div class="col-xs-12 col-sm-8">
										<accommodationsearch:sortSelect accommodationSearchParams="${accommodationSearchParams}" accommodationSearchResponse="${accommodationSearchResponse}" />
									</div>
								</div>
							</div>
						</c:if>
					</div>
					<%-- Begin Accommodation Results section --%>
					<div class="col-xs-12 results-list">
						<h3 class="h4 pull-left">
							<c:choose>
								<c:when test="${totalNumberOfResults gt 0}">
									<spring:theme code="${totalNumberOfResults eq 1 ? 'text.accommodation.listing.found.hotels.single.number' : 'text.accommodation.listing.found.hotels.number'}" arguments="${totalNumberOfResults}, ${ accommodationSearchResponse.criterion.address.line2}" />
								</c:when>
								<c:otherwise>
									<spring:theme code="text.accommodation.listing.empty.list" />
								</c:otherwise>
							</c:choose>
						</h3>
						<%-- Google Map Results --%>
						<accommodationsearch:propertyMapView />
						<div id="y_showResults" class="${resultsView == 'MAPVIEW' ? 'hidden' : ''}">
							<div class="viewResults">
								<c:choose>
									<c:when test="${resultsView == 'LISTVIEW'}">
										<ul id="y_hotelResults" class="clearfix accommodation-items" aria-label="hotel results">
											<accommodationsearch:propertyListView propertiesListParams="${properties}" />
										</ul>
									</c:when>
									<c:otherwise>
										<ul id="y_hotelResults" class="clearfix accommodation-items grid" aria-label="hotel results">
											<accommodationsearch:propertyGridView propertiesListParams="${properties}" />
										</ul>
									</c:otherwise>
								</c:choose>
								<accommodationsearch:showMore />
							</div>
						</div>
					</div>
					<%-- End Accommodation Results section --%>
				</div>
			</div>
			<div class="col-xs-12 col-sm-3">
				<aside id="sidebar" class="y_reservationSideBar reservation">
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
	<div class="y_customerReviewsModal"></div>
	<reservation:fullReservationOverlay />
</template:page>

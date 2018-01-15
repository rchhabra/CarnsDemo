<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/template"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="packagelisting" tagdir="/WEB-INF/tags/addons/travelcommons/responsive/packagelisting"%>
<%@ taglib prefix="progress" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/progress"%>
<%@ taglib prefix="reservation" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/reservation"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<template:page pageTitle="${pageTitle}">
	<progress:travelBookingProgressBar stage="packages" bookingJourney="${bookingJourney}" />
	<div class="y_packageListingPageParams" data-googleapi="${fn:escapeXml(googleAPIKey)}" data-resultViewType="${fn:escapeXml(resultsViewType)}"></div>
	<c:set var="resultsView" value="${fn:toUpperCase(resultsViewType)}" />
	<div class="container row-offcanvas row-offcanvas-left">
		<div class="row">
			<div class="col-xs-12">
				<h2 class="h2">
					<spring:theme code="text.package.listing.title" text="Select Your Package" />
				</h2>
			</div>
		</div>
		<div class="accommodation-selection-wrap clearfix y_accommodationSelectionSection">
			<div class="col-xs-12 y_nonItineraryContentArea">
				<div class="col-xs-12 col-sm-7 col-sm-offset-3">
					<div class="row">
						<div class="modify-search panel panel-primary col-xs-12 col-sm-10 col-sm-offset-1">
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
				<div class="clearfix deal-results-wrapper">
					<c:set var="urlParameters" value="${urlParameters}" />
					<div id="results-options" class="col-xs-12">
						<div class="col-xs-12 col-sm-4 col-sm-push-8 button-inline">
							<div class="row">
								<c:if test="${totalNumberOfResults gt 0 or not empty packageSearchResponse.criterion.facets or not empty accommodationSearchResponse.criterion.filteredFacets or not empty fn:escapeXml(filterPropertyName) or not empty fn:escapeXml(priceRange)}">
									<div class="col-xs-6 visible-xs-block">
										<button type="button" class="btn btn-default-light col-xs-12" data-toggle="offcanvas">
											<span class="glyphicon glyphicon-align-justify" aria-hidden="true"></span>
											<spring:theme code="text.accommodation.listing.filter" text="Filter" />
										</button>
									</div>
								</c:if>
								<div class="y_resultsViewOptions ${totalNumberOfResults gt 0 ? '' : 'hidden'}">
									<div class="col-xs-6 ${resultsView == 'LISTVIEW' ? 'hidden' : ''}" id="y_packageListingListView">
										<button type="button" class="btn btn-default-light col-xs-12">
											<span class="glyphicon glyphicon-th-list" aria-hidden="true"></span>
											<spring:message code="text.accommodation.listing.result.view.list" text="List view" />
										</button>
									</div>
									<div class="col-xs-6 ${resultsView == 'GRIDVIEW' ? 'hidden' : ''}" id="y_packageListingGridView">
										<button type="button" class="btn btn-default-light col-xs-12 hidden-xs ">
											<span class="glyphicon glyphicon-th" aria-hidden="true"></span>
											<spring:message code="text.accommodation.listing.result.view.grid" text="Grid view" />
										</button>
									</div>
									<div class="col-xs-6 ${resultsView == 'MAPVIEW' ? 'hidden' : ''}" id="y_packageListingMapView">
										<button type="button" class="btn btn-default-light col-xs-12">
											<span class="glyphicon glyphicon-map-marker" aria-hidden="true"></span>
											<spring:message code="text.accommodation.listing.map.view.title" text="Map view" />
										</button>
									</div>
								</div>
							</div>
						</div>
						<div class="col-xs-12 col-sm-8 col-sm-pull-4 ${(resultsView eq null or resultsView == 'MAPVIEW' or totalNumberOfResults eq null or totalNumberOfResults eq 0) ? 'hidden' : ''}" id="y_packageListingSortSelection">
							<div class="row">
								<div class="col-xs-12 col-sm-8">
									<packagelisting:sortSelect accommodationSearchParams="${accommodationSearchParams}" />
								</div>
							</div>
						</div>
					</div>
					<%-- Begin Package Results section --%>
					<div class="col-xs-12 results-list">
						<h3 class="h4 y_totalNumberOfResults">
							<c:choose>
								<c:when test="${totalNumberOfResults gt 0}">

									<spring:theme code="${totalNumberOfResults eq 1 ? 'text.package.listing.found.package.single.number' : 'text.package.listing.found.package.number'}" arguments="${totalNumberOfResults}, ${packageSearchResponse.criterion.address.line2}" />

									<c:set var="stayDateRange" value="${packageSearchResponse.criterion.stayDateRange}" />
									<fmt:formatDate value="${stayDateRange.startTime}" var="formattedCheckInDate" type="both" pattern="dd/MM/yyyy" />
									<fmt:formatDate value="${stayDateRange.endTime}" var="formattedCheckOutDate" type="both" pattern="dd/MM/yyyy" />
									<span>&nbsp;${fn:escapeXml(formattedCheckInDate)} - ${fn:escapeXml(formattedCheckOutDate)}</span>
								</c:when>
								<c:otherwise>
									<spring:theme code="text.package.listing.empty.list" />
								</c:otherwise>
							</c:choose>
						</h3>
						<%-- Google Map Results --%>
						<packagelisting:packageMapView />
						<div id="y_packageListingShowResults" class="${resultsView == 'MAPVIEW' ? 'hidden' : ''}">
							<div class="y_viewResults">
								<c:choose>
									<c:when test="${resultsView == 'LISTVIEW'}">
										<ul id="y_packageResults" class="clearfix deal-items deal-row" aria-label="package search results">
											<packagelisting:packageListView propertiesListParams="${packageSearchResponseProperties}" />
										</ul>
									</c:when>
									<c:otherwise>
										<ul id="y_packageResults" class="clearfix deal-items deal-row deal-grid" aria-label="package search results">
											<packagelisting:packageGridView propertiesListParams="${packageSearchResponseProperties}" />
										</ul>
									</c:otherwise>
								</c:choose>
								<packagelisting:packageShowMore />
							</div>
						</div>
					</div>
					<%-- End Package Results section --%>
				</div>
			</div>
		</div>
	</div>
	<div class="y_customerReviewsModal"></div>
</template:page>

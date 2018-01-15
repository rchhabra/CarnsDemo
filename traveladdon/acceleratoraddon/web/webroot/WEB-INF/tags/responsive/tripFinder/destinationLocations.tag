<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
	<c:choose>
		<c:when test="${fn:length(destinationTransportOfferings) gt 0 && empty city}">
			<c:forEach items="${destinationTransportOfferings}" var="destinationTransportOffering" varStatus="index">
				<div class="panel panel-default panel-accordion">
					<div class="panel-heading" role="tab" id="heading-${fn:escapeXml(index.count)}">
						<h2 class="panel-title">
							<a data-toggle="modal" href="#y_fareFinderModal" data-destination-code="${fn:escapeXml(destinationTransportOffering.sector.destination.code)}" data-destination-name="${fn:escapeXml(destinationTransportOffering.sector.destination.location.name)} - ${fn:escapeXml(destinationTransportOffering.sector.destination.name)} (${fn:escapeXml(destinationTransportOffering.sector.destination.code)})">
								<c:out value="${destinationTransportOffering.destinationLocationCity}"></c:out>
							</a>
							<button class="show-hide-button collapsed" data-toggle="collapse" data-parent="#accordion" data-target="#collapse-${fn:escapeXml(index.count)}" aria-expanded="true" aria-controls="collapse-${fn:escapeXml(index.count)}">
								<spring:theme code="tripfinder.destination.location.button.collapseExpand" text="Collapse / Expand" />
							</button>
						</h2>
					</div>
					<div id="collapse-${fn:escapeXml(index.count)}" class="panel-collapse collapse" role="tabpanel" aria-labelledby="heading-${fn:escapeXml(index.count)}">
						<c:set var="cityName" value="${destinationTransportOffering.sector.destination.location.code}" />
						<div class="panel-body">
							<c:set var="component" value="TripFinderCityCMSParagraph${cityName}" />
							<c:set var="cityComponentHtml">
								<cms:component uid="${component}" />
							</c:set>
							<c:if test="${not empty cityComponentHtml}">
								<div class="search-btn col-xs-12 col-sm-6 col-md-4">
									<button class="btn btn-primary btn-block bottom-align" data-toggle="modal" data-target="#y_fareFinderModal" data-destination-code="${fn:escapeXml(destinationTransportOffering.sector.destination.code)}" data-destination-name="${fn:escapeXml(destinationTransportOffering.sector.destination.location.name)} - ${fn:escapeXml(destinationTransportOffering.sector.destination.name)} (${fn:escapeXml(destinationTransportOffering.sector.destination.code)})">
										<spring:theme code="tripfinder.destination.location.button.searchFlights" text="Search Flights" />
									</button>
								</div>
								<cms:component uid="${component}" />
							</c:if>
							<div class="search-btn col-xs-12 col-sm-6 col-md-4">
								<button class="btn btn-primary btn-block bottom-align" data-toggle="modal" data-target="#y_fareFinderModal" data-destination-code="${fn:escapeXml(destinationTransportOffering.sector.destination.code)}" data-destination-name="${fn:escapeXml(destinationTransportOffering.sector.destination.location.name)} - ${fn:escapeXml(destinationTransportOffering.sector.destination.name)} (${fn:escapeXml(destinationTransportOffering.sector.destination.code)})">
									<spring:theme code="tripfinder.destination.location.button.searchFlights" text="Search Flights" />
								</button>
							</div>
						</div>
					</div>
				</div>
			</c:forEach>
		</c:when>
		<c:when test="${fn:length(destinationTransportOfferings) gt 0 && not empty city}">
			<c:forEach items="${destinationTransportOfferings}" var="destinationTransportOffering" varStatus="index">
				<c:set var="cityName" value="${destinationTransportOffering.sector.destination.location.code}" />
				<c:if test="${cityName eq city}">
					<div class="panel panel-default panel-accordion">
						<div class="panel-heading" role="tab" id="heading-${fn:escapeXml(index.count)}">
							<h2 class="panel-title">
								<a data-toggle="modal" href="#y_fareFinderModal" data-destination-code="${fn:escapeXml(destinationTransportOffering.sector.destination.code)}" data-destination-name="${fn:escapeXml(destinationTransportOffering.sector.destination.location.name)} - ${fn:escapeXml(destinationTransportOffering.sector.destination.name)} (${fn:escapeXml(destinationTransportOffering.sector.destination.code)})">
									<c:out value="${destinationTransportOffering.destinationLocationCity}"></c:out>
								</a>
							</h2>
						</div>
						<div role="tabpanel" aria-labelledby="heading-${fn:escapeXml(index.count)}">
							<div class="panel-body">
								<c:set var="component" value="TripFinderCityCMSParagraph${cityName}" />
								<c:set var="cityComponentHtml">
									<cms:component uid="${component}" />
								</c:set>
								<c:if test="${not empty cityComponentHtml}">
									<div class="search-btn col-xs-12 col-sm-6 col-md-4">
										<button class="btn btn-primary btn-block bottom-align" data-toggle="modal" data-target="#y_fareFinderModal" data-destination-code="${fn:escapeXml(destinationTransportOffering.sector.destination.code)}" data-destination-name="${fn:escapeXml(destinationTransportOffering.sector.destination.location.name)} - ${fn:escapeXml(destinationTransportOffering.sector.destination.name)} (${fn:escapeXml(destinationTransportOffering.sector.destination.code)})">
											<spring:theme code="tripfinder.destination.location.button.searchFlights" text="Search Flights" />
										</button>
									</div>
									<cms:component uid="${component}" />
								</c:if>
								<div class="search-btn col-xs-12 col-sm-6 col-md-4">
									<button class="btn btn-primary btn-block bottom-align" data-toggle="modal" data-target="#y_fareFinderModal" data-destination-code="${fn:escapeXml(destinationTransportOffering.sector.destination.code)}" data-destination-name="${fn:escapeXml(destinationTransportOffering.sector.destination.location.name)} - ${fn:escapeXml(destinationTransportOffering.sector.destination.name)} (${fn:escapeXml(destinationTransportOffering.sector.destination.code)})">
										<spring:theme code="tripfinder.destination.location.button.searchFlights" text="Search Flights" />
									</button>
								</div>
							</div>
						</div>
					</div>
				</c:if>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<div class="alert alert-warning" role="alert">
				<spring:theme code="tripfinder.destination.location.not.found" text="No Result Found." />
			</div>
		</c:otherwise>
	</c:choose>
</div>

<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ attribute name="itinerary" required="true" type="de.hybris.platform.commercefacades.travel.ItineraryData"%>
<%@ attribute name="itineraryPricingInfo" required="true" type="de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData"%>
<%@ attribute name="pricedItineraryIndex" required="true" type="java.lang.Integer"%>
<%@ attribute name="originDestinationRefNum" type="java.lang.Integer"%>
<%@ taglib prefix="packageDetails" tagdir="/WEB-INF/tags/addons/travelcommons/responsive/packagedetails"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:set var="numberOfConnections" value="${fn:length(itinerary.originDestinationOptions[0].transportOfferings)}" />
<c:choose>
	<c:when test="${numberOfConnections > 1}">
		<c:set var="firstOffering" value="${itinerary.originDestinationOptions[0].transportOfferings[0]}" />
		<c:set var="lastOffering" value="${itinerary.originDestinationOptions[0].transportOfferings[(numberOfConnections-1)]}" />
	</c:when>
	<c:otherwise>
		<c:set var="firstOffering" value="${itinerary.originDestinationOptions[0].transportOfferings[0]}" />
		<c:set var="lastOffering" value="${firstOffering}" />
	</c:otherwise>
</c:choose>
<div class="journey-wrapper">
	<div class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title">${fn:escapeXml(firstOffering.sector.origin.name)}&nbsp;(${fn:escapeXml(firstOffering.sector.origin.code)})
				<span aria-hidden="true" class="glyphicon glyphicon-arrow-right"></span>
				${fn:escapeXml(lastOffering.sector.destination.name)}&nbsp;(${fn:escapeXml(lastOffering.sector.destination.code)})
			</h3>
		</div>
		<div class="panel-body">
			<div class="col-xs-12">
				<div class="journey-details row">
					<dl class="passenger-details col-xs-6 col-sm-3">
						<dt>
							<spring:theme code="text.package.details.transport.passengers" text="Passengers" />
						</dt>
						<c:forEach var="ptcFareBreakdown" items="${itineraryPricingInfo.ptcFareBreakdownDatas}">
							<c:if test="${ptcFareBreakdown.passengerTypeQuantity.quantity > 0}">
								<dd>${fn:escapeXml(ptcFareBreakdown.passengerTypeQuantity.quantity)}&nbsp;&times;&nbsp;${fn:escapeXml(ptcFareBreakdown.passengerTypeQuantity.passengerType.name)}</dd>
							</c:if>
						</c:forEach>
					</dl>
					<dl class="depart-details col-xs-6 col-sm-3">
					    <c:if test="${originDestinationRefNum == 0}">
					        <input id="y_selectedDepartureTime_${originDestinationRefNum}" type="hidden" value="${firstOffering.departureTime.time}" />
					    </c:if>
						<dt>
							<spring:theme code="text.package.details.transport.departs" text="Departs" />
						</dt>
						<dd>
							<fmt:formatDate pattern="${dateFormat}" value="${firstOffering.departureTime}" />
						</dd>
						<dd>
							<fmt:formatDate pattern="${timeFormat}" value="${firstOffering.departureTime}" />
						</dd>
					</dl>
					<dl class="arrive-detail col-xs-6 col-sm-3">
					    <c:if test="${originDestinationRefNum == 1}">
					        <input id="y_selectedArrivalTime_${originDestinationRefNum}" type="hidden" value="${lastOffering.arrivalTime.time}" />
					    </c:if>
						<dt>
							<spring:theme code="text.package.details.transport.arrives" text="Arrives" />
						</dt>
						<dd>
							<fmt:formatDate pattern="${dateFormat}" value="${lastOffering.arrivalTime}" />
						</dd>
						<dd>
							<fmt:formatDate pattern="${timeFormat}" value="${lastOffering.arrivalTime}" />
						</dd>
					</dl>
					<dl class="travel-duration col-xs-6 col-sm-3">
						<dt>${fn:escapeXml(itineraryPricingInfo.bundleTypeName)}</dt>
						<dd>
							<c:choose>
								<c:when test="${numberOfConnections > 1}">
                                    ${numberOfConnections - 1}&nbsp;<spring:theme code="${numberOfConnections > 2 ? 'fareselection.offering.stops' : 'fareselection.offering.stop'}" />
								</c:when>
								<c:otherwise>
									<spring:theme code="fareselection.offering.nonstop" />
									<c:if test="${fn:length(itinerary.originDestinationOptions[0].transportOfferings[0].stopLocations) > 0}">
										<c:set var="stopLocations">
											<c:forEach items="${itinerary.originDestinationOptions[0].transportOfferings[0].stopLocations}" var="stop" varStatus="stopIdx">
                                                ${fn:escapeXml(stop.code)}${!stopIdx.last ? ',' : ''}&nbsp;
                                            </c:forEach>
										</c:set>
										<span>
											<a href="#" class="info-tooltip" data-toggle="tooltip" title="This flight will be stopping at ${fn:escapeXml(stopLocations)}" tabindex="0">info</a>
										</span>
									</c:if>
								</c:otherwise>
							</c:choose>
						</dd>
						<dd>
							<spring:theme code="text.package.details.transport.duration" text="Duration" />
							&nbsp;
							<c:if test="${not empty itinerary.duration['transport.offering.status.result.days'] && itinerary.duration['transport.offering.status.result.days'] != 0}">${fn:escapeXml(itinerary.duration['transport.offering.status.result.days'])}&nbsp;<spring:theme code="transport.offering.status.result.days" />
							</c:if>
							<c:if test="${not empty itinerary.duration['transport.offering.status.result.hours'] && itinerary.duration['transport.offering.status.result.hours'] != 0}">${fn:escapeXml(itinerary.duration['transport.offering.status.result.hours'])}&nbsp;
                                <spring:theme code="transport.offering.status.result.hours" />
							</c:if>
							&nbsp;${fn:escapeXml(itinerary.duration['transport.offering.status.result.minutes'])}&nbsp;
							<spring:theme code="transport.offering.status.result.minutes" />
						</dd>
					</dl>
					<packageDetails:selectedTransportOfferingFlightDetails itineraryData="${itinerary}" noOfConnections="${numberOfConnections}" pricedItineraryIndex="${pricedItineraryIndex}" />
				</div>
			</div>
		</div>
	</div>
</div>

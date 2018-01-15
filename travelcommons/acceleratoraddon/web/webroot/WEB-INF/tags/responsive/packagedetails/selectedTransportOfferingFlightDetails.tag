<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="itineraryData" required="true" type="de.hybris.platform.commercefacades.travel.ItineraryData"%>
<%@ attribute name="pricedItineraryIndex" required="true" type="java.lang.Integer"%>
<%@ attribute name="noOfConnections" required="true" type="java.lang.Integer"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:if test="${noOfConnections > 1}">
	<c:set var="flightDetailsCollapseId" value="#selected-collapse${pricedItineraryIndex}" />
	<c:set var="flightDetailsCollapse" value="selected-collapse${pricedItineraryIndex}" />
	<div class="col-xs-12">
		<div class="row" role="tab" id="headingTwo">
			<a role="button" data-toggle="collapse" data-parent="#accordion" href="${flightDetailsCollapseId}" aria-expanded="true" aria-controls="${fn:escapeXml(flightDetailsCollapse)}" class="info-trigger y_fareResultInfoTrigger">
				<i class="glyphicon glyphicon-info-sign"></i>
				<spring:theme code="fareselection.journeydetails" />
			</a>
		</div>
	</div>
	<div class="col-xs-12 stops-wrapper">
		<div class="row">
			<div class="col-xs-12 col-sm-9">
				<div id="${fn:escapeXml(flightDetailsCollapse)}" class="clearfix panel-collapse collapse" role="tabpanel" aria-labelledby="headingOne">
					<div class="col-xs-12">
						<div class="row">
							<c:forEach items="${itineraryData.originDestinationOptions}" var="originDestinationOption" varStatus="optionIdx">
								<c:forEach items="${originDestinationOption.transportOfferings}" var="transportOffering" varStatus="offeringIdx">
									<div class="clearfix">
										<dl class="from-details col-xs-6 col-sm-4">
											<dt class="sr-only">
												<spring:theme code="sr.fareselection.fromlocation" />
											</dt>
											<dd class="location-info heading">${fn:escapeXml(transportOffering.sector.origin.name)}&nbsp;(${fn:escapeXml(transportOffering.sector.origin.code)})</dd>
											<dt class="sr-only">
												<spring:theme code="sr.fareselection.departuretime" />
											</dt>
											<dd class="time-info">
												<fmt:formatDate pattern="${pricedItineraryDateFormat}" value="${transportOffering.departureTime}" />
											</dd>
										</dl>
										<dl class="to-details col-xs-6 col-sm-4">
											<dt class="sr-only">
												<spring:theme code="sr.fareselection.destinationlocation" />
											</dt>
											<dd class="location-info heading">${fn:escapeXml(transportOffering.sector.destination.name)}&nbsp;(${fn:escapeXml(transportOffering.sector.destination.code)})</dd>
											<dt class="sr-only">
												<spring:theme code="sr.fareselection.arrivaltime" />
											</dt>
											<dd class="time-info">
												<fmt:formatDate pattern="${pricedItineraryDateFormat}" value="${transportOffering.arrivalTime}" />
											</dd>
										</dl>
										<dl class="flight-duration col-xs-12 col-sm-4">
											<dt class="sr-only">
												<spring:theme code="sr.fareselection.flightduration" />
											</dt>
											<dd class="duration-info col-reset">
												<c:if test="${not empty transportOffering.duration['transport.offering.status.result.days'] && transportOffering.duration['transport.offering.status.result.days'] != 0}">${fn:escapeXml(transportOffering.duration['transport.offering.status.result.days'])}&nbsp;<spring:theme
														code="transport.offering.status.result.days" />
												</c:if>
												<c:if test="${not empty transportOffering.duration['transport.offering.status.result.hours'] && transportOffering.duration['transport.offering.status.result.hours'] != 0}">${fn:escapeXml(transportOffering.duration['transport.offering.status.result.hours'])}&nbsp;
												<spring:theme code="transport.offering.status.result.hours" />
												</c:if>
												&nbsp;${fn:escapeXml(transportOffering.duration['transport.offering.status.result.minutes'])}&nbsp;
												<spring:theme code="transport.offering.status.result.minutes" />
												<c:if test="${fn:length(transportOffering.stopLocations) > 0}">
													<c:set var="stopLocations">
														<c:forEach items="${transportOffering.stopLocations}" var="stop" varStatus="stopIdx">
														${fn:escapeXml(stop.code)}${!stopIdx.last ? ',' : ''}&nbsp;
													</c:forEach>
													</c:set>
													<c:set var="stopInfoMessage">
														<spring:theme code="transport.offering.stop.info.msg" />
													</c:set>
													<span>
														<a href="#" class="info-tooltip" data-toggle="tooltip" title="${fn:escapeXml(stopInfoMessage)}&nbsp;${fn:escapeXml(stopLocations)}" tabindex="0">info</a>
													</span>
												</c:if>
											</dd>
										</dl>
									</div>
									<div class="clearfix">
										<ul class="flight-details flight-details-stop col-xs-12">
											<li class="flight-number">
												<span class="heading">
													<spring:theme code="fareselection.flight" />
												</span>
												&nbsp;${fn:escapeXml(transportOffering.travelProvider.code)}${fn:escapeXml(transportOffering.number)}&nbsp;${fn:escapeXml(transportOffering.transportVehicle.vehicleInfo.name)}
											</li>
										</ul>
										<c:if test='${!offeringIdx.last}'>
											<span class="col-xs-12 one-stop-divide y_fareResultStopDivide glyphicon glyphicon-transfer"></span>
										</c:if>
									</div>
								</c:forEach>
							</c:forEach>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</c:if>

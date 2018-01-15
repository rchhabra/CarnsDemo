<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="fareSelection" required="true" type="de.hybris.platform.commercefacades.travel.FareSelectionData"%>
<%@ attribute name="refNumber" required="true" type="java.lang.Integer"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fareselection" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/fareselection"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<c:choose>
	<c:when test="${refNumber == outboundRefNumber}">
		<c:set var="sortby" value="outbound-sort-by" />
		<c:set var="rowresult" value="outbound-row-result-" />
	</c:when>
	<c:when test="${refNumber == inboundRefNumber}">
		<c:set var="sortby" value="inbound-sort-by" />
		<c:set var="rowresult" value="inbound-row-result-" />
	</c:when>
</c:choose>
<c:set var="noOfOptions" value="${refNumber == outboundRefNumber ? noOfOutboundOptions : noOfInboundOptions }" />
<c:set var="dates" value="${refNumber == outboundRefNumber ? outboundDates : inboundDates }" />
<c:set var="linksForTabs" value="${refNumber == outboundRefNumber ? outboundTabLinks : inboundTabLinks }" />

<div class="nav-tabs-wrapper y_fareResultNavTabs">
	<a href="${fn:escapeXml(linksForTabs[3])}" class="next y_next">&#10095;</a>
	<a href="${fn:escapeXml(linksForTabs[1])}" class="prev y_previous">&#10094;</a>
	<%-- Nav tabs --%>
	<ul class="nav nav-tabs" role="tablist">
		<c:forEach items="${dates}" var="tabDate" varStatus="tabIdx">
			<li role="presentation" class="y_tab ${tabIdx.index == 2 ? 'active' : ''}">
				<a href="${fn:escapeXml(linksForTabs[tabIdx.index])}" dateValue="${fn:escapeXml(tabDate.time)}">
					<c:choose>
						<c:when test="${tabIdx.index == 2}">
							<span class="tab-date"><fmt:formatDate pattern="${fareSelectionTabDateFormat}" value="${tabDate}" /></span>
							<c:set var="lowestPriceForDay" value="${refNumber == outboundRefNumber ? outLowestPrice : inLowestPrice}" />
							<span class="tab-price"> <c:if test="${not empty lowestPriceForDay}">
									<spring:theme code="fareselection.from" />&nbsp;${fn:escapeXml(lowestPriceForDay.formattedValue)}
							</c:if>
							</span>
						</c:when>
						<c:otherwise>
							<span class="tab-date"><fmt:formatDate pattern="${fareSelectionTabDateFormat}" value="${tabDate}" /></span>
						</c:otherwise>
					</c:choose>
				</a>
			</li>
		</c:forEach>
	</ul>
</div>
<%-- Tab panes --%>
<div class="y_fareResultTabContent tab-content">
	<c:forEach items="${dates}" var="tabDate" varStatus="tabIdx">
		<c:choose>
			<c:when test="${tabIdx.index != 2}">
				<div class="tab-pane fade">
					<p>
						<spring:theme code="fareselection.noofferings" />
					</p>
				</div>
			</c:when>
			<c:otherwise>
				<div role="tabpanel" class="tab-pane fade in active">
					<div class="clearfix">
						<div class="y_fareResultSort form-inline clearfix">
							<label for="${fn:escapeXml(sortby)}">
								<spring:theme code="fareselection.sorting.dropdown.sort.by" />
							</label>
							<c:set var="selectedSortingParam" value="${fn:escapeXml(selectedSorting != null ? selectedSorting : 'DEPARTURE_TIME' )}" />
							<select class="form-control y_sorting-options" id="${fn:escapeXml(sortby)}" refNumber="${fn:escapeXml(refNumber)}">
								<c:forEach items="${sortingParameters}" var="parameter">
									<option value="${fn:escapeXml(parameter)}" ${parameter eq selectedSortingParam ? 'selected' : ''}><spring:theme code="fareselection.sorting.${parameter}" /></option>
								</c:forEach>
							</select>
						</div>
					</div>
					<ul class="list-unstyled clearfix" id="flight-option-${fn:escapeXml(refNumber)}">
						<%-- this part should be refreshed --%>
						<%-- Flight option row--%>
						<c:choose>
							<c:when test="${empty fareSelection.pricedItineraries || noOfOptions == 0}">
								<div>
									<spring:theme code="fareselection.noofferings" />
								</div>
							</c:when>
							<c:otherwise>
								<c:forEach items="${fareSelection.pricedItineraries}" var="pricedItinerary" varStatus="idx">
									<c:if test="${pricedItinerary.available && pricedItinerary.originDestinationRefNumber == refNumber}">
										<c:set var="resultrowid" value="${rowresult}${idx.index}" />
										<li id="${fn:escapeXml(resultrowid)}" class="y_pricedItinerary" value="${fn:escapeXml(pricedItinerary.id)}" style="display: none;">
											<c:set var="numberOfConnections" value="${fn:length(pricedItinerary.itinerary.originDestinationOptions[0].transportOfferings)}" />
											<div class="flight-option y_fareResultTravelOption">
												<div class="travel-details y_fareResultTravelDetails col-xs-12 col-sm-7">
													<div class="col-xs-9 col-sm-12">
														<c:choose>
															<c:when test="${numberOfConnections > 1}">
																<c:set var="firstOffering" value="${pricedItinerary.itinerary.originDestinationOptions[0].transportOfferings[0]}" />
																<c:set var="lastOffering" value="${pricedItinerary.itinerary.originDestinationOptions[0].transportOfferings[(numberOfConnections-1)]}" />
															</c:when>
															<c:otherwise>
																<c:set var="firstOffering" value="${pricedItinerary.itinerary.originDestinationOptions[0].transportOfferings[0]}" />
																<c:set var="lastOffering" value="${firstOffering}" />
															</c:otherwise>
														</c:choose>
														<dl class="from-details col-xs-6 col-sm-4">
															<dt class="sr-only">
																<spring:theme code="sr.fareselection.fromlocation" />
															</dt>
															<dd class="location-info heading">${fn:escapeXml(firstOffering.sector.origin.location.name)}&nbsp;(${fn:escapeXml(firstOffering.sector.origin.code)})</dd>
															<dt class="sr-only">
																<spring:theme code="sr.fareselection.departuretime" />
															</dt>
															<dd class="time-info">
																<fmt:formatDate pattern="${pricedItineraryDateFormat}" value="${firstOffering.departureTime}" />
															</dd>
														</dl>
														<dl class="to-details col-xs-6 col-sm-4">
															<dt class="sr-only">
																<spring:theme code="sr.fareselection.destinationlocation" />
															</dt>
															<dd class="location-info heading">${fn:escapeXml(lastOffering.sector.destination.location.name)}&nbsp;(${fn:escapeXml(lastOffering.sector.destination.code)})</dd>
															<dt class="sr-only">
																<spring:theme code="sr.fareselection.arrivaltime" />
															</dt>
															<dd class="time-info">
																<fmt:formatDate pattern="${pricedItineraryDateFormat}" value="${lastOffering.arrivalTime}" />
															</dd>
														</dl>
														<dl class="flight-duration col-xs-12 col-sm-4">
															<dt class="sr-only">
																<spring:theme code="sr.fareselection.flightduration" />
															</dt>
															<dd class="duration-info col-xs-6 col-sm-7 col-reset">
																<c:if test="${not empty pricedItinerary.itinerary.duration['transport.offering.status.result.days'] && pricedItinerary.itinerary.duration['transport.offering.status.result.days'] != 0}">${fn:escapeXml(pricedItinerary.itinerary.duration['transport.offering.status.result.days'])}&nbsp;<spring:theme
																		code="transport.offering.status.result.days" />
																</c:if>
																<c:if test="${not empty pricedItinerary.itinerary.duration['transport.offering.status.result.hours'] && pricedItinerary.itinerary.duration['transport.offering.status.result.hours'] != 0}">${fn:escapeXml(pricedItinerary.itinerary.duration['transport.offering.status.result.hours'])}&nbsp;
																	<spring:theme code="transport.offering.status.result.hours" />
																</c:if>
																&nbsp;${fn:escapeXml(pricedItinerary.itinerary.duration['transport.offering.status.result.minutes'])}&nbsp;
																<spring:theme code="transport.offering.status.result.minutes" />
															</dd>
															<dd class="duration-info col-xs-6 col-sm-5 col-reset">
																<c:choose>
																	<c:when test="${numberOfConnections > 1}">
    																	${fn:escapeXml(numberOfConnections - 1)}&nbsp;<spring:theme code="${numberOfConnections > 2 ? 'fareselection.offering.stops' : 'fareselection.offering.stop'}" />
																	</c:when>
																	<c:otherwise>
																		<spring:theme code="fareselection.offering.nonstop" />
																		<c:if test="${fn:length(pricedItinerary.itinerary.originDestinationOptions[0].transportOfferings[0].stopLocations) > 0}">
																			<c:set var="stopLocations">
																				<c:forEach items="${pricedItinerary.itinerary.originDestinationOptions[0].transportOfferings[0].stopLocations}" var="stop" varStatus="stopIdx">
																					${fn:escapeXml(stop.code)}${!stopIdx.last ? ',' : ''}&nbsp;
																				</c:forEach>
																			</c:set>
																			<span> <a href="#" class="info-tooltip" data-toggle="tooltip" title="This flight will be stopping at ${fn:escapeXml(stopLocations)}" tabindex="0">info</a>
																			</span>
																		</c:if>
																	</c:otherwise>
																</c:choose>
															</dd>
														</dl>
													</div>
													<div class="col-xs-3 from-price">
														<button class="select-class-trigger y_fareResultPriceButton">
															<span><spring:theme code="fareselection.from" /></span><span class="y_fareResultLowestPrice from-price"></span><span class="arrow">&#9656;</span>
														</button>
														<button class="select-class-trigger y_fareResultChangeButton change-selection hidden">
															<spring:theme code="fareselection.change" />
														</button>
													</div>
													<fareselection:flightDetails pricedItinerary="${pricedItinerary}" noOfConnections="${numberOfConnections}" />
												</div>
												<div class="flight-class-select y_fareResultClassSelectGroup col-xs-12 col-sm-5 hide-on-mobile">
													<fieldset>
														<fareselection:offeringListBundles bundleTypes="STANDARD" pricedItinerary="${pricedItinerary}" refNumber="${refNumber}" name="refNumber-${refNumber}" />
														<fareselection:offeringListBundles bundleTypes="NON_STANDARD" pricedItinerary="${pricedItinerary}" refNumber="${refNumber}" name="refNumber-${refNumber}" />
													</fieldset>
												</div>
											</div>
										</li>
										<c:set var="firstElement" value="${false}" />
									</c:if>
								</c:forEach>
							</c:otherwise>
						</c:choose>
						<%-- / Flight option row--%>
					</ul>
				</div>
			</c:otherwise>
		</c:choose>
	</c:forEach>
	<div class="col-xs-12 y_fareResultShowMore">
		<c:if test="${noOfOptions > 5}">
			<button type="submit" class="btn btn-primary col-xs-12 col-sm-5 col-sm-offset-7">
				<spring:theme code="fareselection.showmore" />
			</button>
		</c:if>
	</div>
</div>

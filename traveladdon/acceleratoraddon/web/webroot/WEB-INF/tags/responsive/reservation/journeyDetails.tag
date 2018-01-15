<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ attribute name="itinerary" required="true" type="de.hybris.platform.commercefacades.travel.ItineraryData" %>
<spring:htmlEscape defaultHtmlEscape="true" />

<c:forEach items="${itinerary.originDestinationOptions}" var="originDestinationOption" varStatus="optionIdx">
    <c:set var="multiSector" value="${fn:length(originDestinationOption.transportOfferings) > 1}" />
    <c:forEach items="${originDestinationOption.transportOfferings}" var="transportOffering" varStatus="offeringIdx">
        <div class="row">
            <div class="col-xs-10 col-sm-9 col-md-9 col-lg-10">
                <h3 class="h5">${fn:escapeXml(transportOffering.sector.origin.code)}&nbsp;>&nbsp;${fn:escapeXml(transportOffering.sector.destination.code)}:</h3>
            </div>
            <c:if test="${not empty transportOffering.stopLocations}">
                <c:set var="stopLocations">
                    <c:forEach items="${transportOffering.stopLocations}" var="stop" varStatus="stopIdx">
                        ${fn:escapeXml(stop.code)}${!stopIdx.last ? ',' : ''}&nbsp;
                    </c:forEach>
                </c:set>
                <div class="col-xs-2 col-sm-3 col-md-2">
                    <button type="button" class="info-btn" data-container="body" data-toggle="popover" data-trigger="focus"
                            data-placement="left" data-content="This flight will be stopping at ${fn:escapeXml(stopLocations)}">
                        <i class="glyphicon glyphicon-info-sign"><span class="sr-only"><spring:theme
                                code="sr.itinerary.information"/></span></i>
                    </button>
                </div>
            </c:if>
        </div>
        <div class="col-xs-12 col-sm-6 col-md-6">
            <dl>
                <dt>
                    <spring:theme code="reservation.journey.depart"/>
                </dt>
                <dd>
                    <fmt:formatDate pattern="${dateFormat}" value="${transportOffering.departureTime}"/>&nbsp;<fmt:formatDate pattern="${timeFormat}" value="${transportOffering.departureTime}"/>
                </dd>
            </dl>
        </div>
        <div class="col-xs-12 col-sm-6 col-md-6">
            <dl>
                <dt>
                    <spring:theme code="reservation.journey.arrival"/>
                </dt>
                <dd>
                    <fmt:formatDate pattern="${dateFormat}" value="${transportOffering.arrivalTime}"/>&nbsp;<fmt:formatDate pattern="${timeFormat}" value="${transportOffering.arrivalTime}"/>
                </dd>
            </dl>
        </div>
    </c:forEach>
</c:forEach>

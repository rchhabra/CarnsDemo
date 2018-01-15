<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="reservation" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/reservation" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/format" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:if test="${reservation.bookingStatusCode ne 'CANCELLED'}">
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="y_transportReservationComponent">
    <input type="hidden" value="${fn:escapeXml(component.uid)}" id="y_transportReservationComponentId"/>
    <c:if test="${not empty reservation}">
        <div class="panel panel-primary panel-list booking room-details clearfix">
            <c:forEach items="${reservation.reservationItems}" var="item" varStatus="itemIdx">
                <div class="journey-wrapper">
                    <div class="panel-default my-account-secondary-panel">
                        <div class="panel-heading">
                            <h3 class="panel-title">${fn:escapeXml(item.reservationItinerary.route.origin.code)}&puncsp;
                                <span aria-hidden="true" class="glyphicon glyphicon-arrow-right"></span>
                                    ${fn:escapeXml(item.reservationItinerary.route.destination.code)} - ${fn:escapeXml(item.reservationPricingInfo.itineraryPricingInfo.bundleTypeName)}
                            </h3>
                        </div>
                        <div class="panel-body" id="leg-${fn:escapeXml(itemIdx.index)}">
                            <fieldset class="fieldset">
                                <div class="row">
                                    <div class="col-xs-12 journey-details">
                                        <reservation:journeyDetails itinerary="${item.reservationItinerary}"/>
                                        <div class="col-xs-12">
                                            <reservation:ptcBreakdown
                                                    ptcBreakdownList="${item.reservationPricingInfo.itineraryPricingInfo.ptcFareBreakdownDatas}"/>
                                        </div>
                                        <reservation:ancillaries
                                                offerBreakdowns="${item.reservationPricingInfo.offerBreakdowns}"/>
                                        <span class="cart-total pull-right">
                                            <spring:theme code="reservation.total"/>:&nbsp;<format:price
                                                priceData="${item.reservationPricingInfo.totalFare.totalBaseExtrasPrice}"/>
                                        </span>
                                    </div>
                                </div>
                            </fieldset>
                        </div>
                    </div>
                </div>
            </c:forEach>
            <c:if test="${not empty reservation.offerBreakdowns}">
                <div class="journey-wrapper">
                    <div class="panel-default my-account-secondary-panel">
                        <reservation:globalancillaries reservation="${reservation}"/>
                    </div>
                </div>
            </c:if>
            <div class="journey-wrapper">
                <div class="panel-default my-account-secondary-panel">
                    <reservation:summary/>
                </div>
            </div>
        </div>
    </c:if>
</div>
</c:if>

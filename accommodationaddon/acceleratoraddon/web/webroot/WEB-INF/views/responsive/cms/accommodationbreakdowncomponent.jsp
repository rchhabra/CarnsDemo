<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="accommodationReservation"
           tagdir="/WEB-INF/tags/addons/accommodationaddon/responsive/accommodationreservation" %>
<spring:htmlEscape defaultHtmlEscape="true" />

<c:if test="${reservation.bookingStatusCode ne 'CANCELLED' && reservation != null}">
    <div class="journey-wrapper">
        <div class="panel panel-default my-account-secondary-panel room-details">
            <div class="panel-heading">
                <h3 class="panel-title"><spring:theme code="text.cms.accommodationbreakdown.stayat"
                                                      text="Stay at"/>&puncsp;${fn:escapeXml(reservation.accommodationReference.accommodationOfferingName)}</h3>
            </div>
            <div class="panel-body" id="room-details-section">
                <c:forEach items="${reservation.roomStays}" var="roomStay" varStatus="idx">
                    <div class="col-xs-12">
                        <accommodationReservation:roomTypeBreakdown roomStay="${roomStay}" index="${idx.index}"/>
                        <accommodationReservation:roomstaybreakdown roomStay="${roomStay}" index="${idx.index}"/>
                        <accommodationReservation:selectedRoomPreferences roomStay="${roomStay}" />
                    </div>
                </c:forEach>

                <div class="col-xs-12">
                    <span class="cart-total pull-right"><spring:theme code="text.cms.accommodationbreakdown.total"
                                                                  text="Total:"/>&puncsp;${fn:escapeXml(reservation.totalRate.actualRate.formattedValue)}</span>
                </div>
            </div>
        </div>
    </div>
</c:if>

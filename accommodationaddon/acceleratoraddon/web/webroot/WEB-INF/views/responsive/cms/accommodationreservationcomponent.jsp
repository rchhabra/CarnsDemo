<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="accommodationReservation"
           tagdir="/WEB-INF/tags/addons/accommodationaddon/responsive/accommodationreservation" %>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:if test="${accommodationReservationData.bookingStatusCode ne 'CANCELLED'}">
<div class="y_accommodationReservationComponent">
    <input type="hidden" value="${fn:escapeXml(component.uid)}" id="y_accommodationReservationComponentId"/>
    <c:if test="${not empty accommodationReservationData}">
        <div class="panel panel-primary panel-list booking room-details clearfix">
            <div class="journey-wrapper">
                <div class="panel-default my-account-secondary-panel">
                    <div class="panel-heading">
                        <h3 class="panel-title"><spring:theme code="text.cms.accommodationbreakdown.stayat"
                                                              text="Stay at"/>&puncsp;${fn:escapeXml(accommodationReservationData.accommodationReference.accommodationOfferingName)}</h3>
                    </div>
                    <div class="panel-body">
                        <c:forEach items="${accommodationReservationData.roomStays}" var="roomStay" varStatus="idx">
                            <div class="col-xs-12">
                                <accommodationReservation:roomTypeBreakdown roomStay="${roomStay}" index="${idx.index}"/>
                                <accommodationReservation:roomstaybreakdown roomStay="${roomStay}" index="${idx.index}"/>
                                <accommodationReservation:selectedRoomPreferences roomStay="${roomStay}" />
                            </div>
                        </c:forEach>
                        <div class="col-xs-12">
                            <span class="cart-total pull-right"><spring:theme
                                    code="text.cms.accommodationbreakdown.total"/>:&nbsp;${fn:escapeXml(accommodationReservationData.totalRate.actualRate.formattedValue)}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </c:if>
</div>
</c:if>

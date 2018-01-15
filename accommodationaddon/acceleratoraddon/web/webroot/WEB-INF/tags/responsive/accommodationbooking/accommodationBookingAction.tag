<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="accommodationBooking" tagdir="/WEB-INF/tags/addons/accommodationaddon/responsive/accommodationbooking"%>
<%@ attribute name="bookingActionResponseData" required="true" type="de.hybris.platform.commercefacades.travel.BookingActionResponseData"%>
<%@ attribute name="actionType" required="true" type="java.lang.String"%>
<%@ attribute name="requestCode" type="java.lang.String"%>
<%@ attribute name="roomStay" type="de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData"%>
<%@ attribute name="actionName" type="java.lang.String"%>
<%@ attribute name="bookingReferenceCode" required="false" type="java.lang.String"%>
<%@ attribute name="accommodationOffering" required="false" type="java.lang.String"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:forEach items="${bookingActionResponseData.accommodationBookingActions}" var="bookingActionData">
	<c:if test="${actionType == bookingActionData.actionType}">
		<c:choose>
			<c:when test="${actionType == 'ADD_REQUEST' || actionType == 'CANCEL_REQUEST' || actionType == 'WRITE_REVIEW'}">
				<c:if test="${ actionType == 'ADD_REQUEST' && bookingActionData.roomStayRefNumber eq roomStay.roomStayRefNumber }">
					<c:set var="actionData" value="${bookingActionData}" />
				</c:if>
				<c:if test="${ actionType == 'WRITE_REVIEW' && bookingActionData.roomStayRefNumber eq roomStay.roomStayRefNumber }">
					<c:set var="actionData" value="${bookingActionData}" />
				</c:if>
				<c:if test="${actionType == 'CANCEL_REQUEST' and bookingActionData.requestCode eq requestCode}">
					<c:set var="actionData" value="${bookingActionData}" />
				</c:if>
			</c:when>
			<c:otherwise>
				<c:set var="actionData" value="${bookingActionData}" />
			</c:otherwise>
		</c:choose>
	</c:if>
</c:forEach>
<c:if test="${actionData.enabled}">
	<c:choose>
		<c:when test="${actionType == 'ADD_REQUEST' || actionType == 'CANCEL_REQUEST' || actionType == 'CHANGE_DATES' || actionType=='WRITE_REVIEW' || actionType == 'AMEND_EXTRAS'}">
			<c:if test="${actionType == 'ADD_REQUEST'}">
				<accommodationBooking:accommodationRequest actionData="${actionData}" />
			</c:if>
			<c:if test="${actionType == 'CANCEL_REQUEST'}">
				<c:url var="cancelRequestUrl" value="${actionData.actionUrl}" />
				<a href="${cancelRequestUrl}" class="close" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</a>
			</c:if>
			<c:if test="${actionType == 'AMEND_EXTRAS'}">
				<c:url var="amendExtrasURL" value="${actionData.actionUrl}?roomStay=${roomStay.roomStayRefNumber}" />
				<a class="btn btn-primary btn-block ${fn:escapeXml(y_class)}" href="${amendExtrasURL}" ${disabled ? 'disabled' : ''}>
					<spring:theme code="button.booking.details.accommodation.booking.action.${actionType}" text="${fn:escapeXml(fn:toLowerCase(actionType))}" />
				</a>
			</c:if>
			<c:if test="${actionType == 'CHANGE_DATES'}">
				<accommodationBooking:changeBookingDates actionData="${actionData}" />
			</c:if>
			<c:if test="${actionType == 'WRITE_REVIEW'}">
				<accommodationBooking:accommodationReview actionData="${actionData}" roomStay="${roomStay}" accommodationOffering="${accommodationOffering}" />
			</c:if>
		</c:when>
		<c:otherwise>
			<c:if test="${actionType == 'CANCEL_BOOKING'}">
				<c:set var="y_class" value="y_cancelBookingButton" />
				<c:set var="disabled" value="true" />
			</c:if>
			<c:if test="${actionType == 'CANCEL_ACCOMMODATION_BOOKING'}">
				<c:set var="y_class" value="y_cancelAccommodationBookingButton" />
				<c:set var="disabled" value="true" />
			</c:if>
			<c:if test="${actionType == 'ADD_ROOM' and !isDealInOrder and isPackageInOrder and isPackageBookingJourney}">
				<c:set var="y_class" value="y_addRoom" />
				<c:set var="isDynamicPackage" value="true" />
			</c:if>
			<c:url value="${actionData.actionUrl}" var="actionUrl" />
			<a class="btn btn-primary btn-block ${fn:escapeXml(y_class)}" href="${isDynamicPackage?'':actionUrl}" ${disabled ? 'disabled' : ''} data-bookingcode="${fn:escapeXml(bookingReferenceCode)}">
				<spring:theme code="button.booking.details.accommodation.booking.action.${actionType}" text="${fn:escapeXml(fn:toLowerCase(actionType))}" />
			</a>
		</c:otherwise>
	</c:choose>
</c:if>

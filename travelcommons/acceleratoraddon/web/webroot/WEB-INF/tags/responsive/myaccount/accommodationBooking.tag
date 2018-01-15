<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="myBooking" required="true" type="de.hybris.platform.commercefacades.accommodation.AccommodationReservationData"%>
<%@ attribute name="isDisplayBookingReferenceCode" required="true" type="java.lang.String"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:if test="${not empty myBooking.roomStays}">
	<c:set var="roomStay" value="${myBooking.roomStays[0]}" />
	<fmt:formatDate value="${roomStay.checkInDate}" pattern="${datePattern}" var="checkInDate" />
	<fmt:formatDate value="${roomStay.checkOutDate}" pattern="${datePattern}" var="checkOutDate" />
	<c:set var="numberOfNights">
		<fmt:parseNumber value="${(roomStay.checkOutDate.time-roomStay.checkInDate.time)/ (1000*60*60*24)}" integerOnly="true" />
	</c:set>
</c:if>
<dl>
	<dt>${fn:escapeXml(myBooking.accommodationReference.accommodationOfferingName)}</dt>
	<dd>
		<c:choose>
			<c:when test="${numberOfNights le 1}">
				<spring:theme code="text.page.mybookings.night" arguments="${numberOfNights}" />
			</c:when>
			<c:otherwise>
				<spring:theme code="text.page.mybookings.nights" arguments="${numberOfNights}" />
			</c:otherwise>
		</c:choose>
		<c:if test="${not empty accommodationRoomMapping}">
			<c:if test="${not empty accommodationRoomMapping[myBooking.code]}">
				<c:forEach items="${accommodationRoomMapping[myBooking.code]}" var="accommodationRoom" varStatus="arIdx">
					<spring:theme code="text.page.mybookings.accommodation.roomName" arguments="${accommodationRoom.value},${accommodationRoom.key}" />
					<c:if test="${not arIdx.last }">
					,
					</c:if>
				</c:forEach>
			</c:if>
		</c:if>
	</dd>
	<dd>
		<spring:theme code="text.page.mybookings.bookingDates" arguments="${checkInDate},${checkOutDate}" />
	</dd>
	<c:if test="${not empty isDisplayBookingReferenceCode && isDisplayBookingReferenceCode}">
		<dd>
			<spring:theme code="text.page.mybooking.bookingreference" arguments="${myBooking.code}" />
		</dd>
	</c:if>
</dl>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ attribute name="myBooking" required="true" type="de.hybris.platform.commercefacades.travel.reservation.data.ReservationData"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:set var="lastReservationItemIdx" value="${fn:length(myBooking.reservationItems)-1}" />
<c:set var="lastTransportOfferingIdx" value="${fn:length(myBooking.reservationItems[lastReservationItemIdx].reservationItinerary.originDestinationOptions[0].transportOfferings)-1}" />
<dl>
	<fmt:formatDate value="${myBooking.reservationItems[0].reservationItinerary.originDestinationOptions[0].transportOfferings[0].departureTime}" pattern="${datePattern}" var="departureDate" />
	<fmt:formatDate value="${myBooking.reservationItems[lastReservationItemIdx].reservationItinerary.originDestinationOptions[0].transportOfferings[lastTransportOfferingIdx].arrivalTime}" pattern="${datePattern}" var="arrivalDate" />
	<dt>${fn:escapeXml(myBooking.reservationItems[0].reservationItinerary.route.destination.name)}</dt>
	<dd>
		<spring:theme code="text.page.mybookings.bookingDates" arguments="${departureDate},${arrivalDate}" />
	</dd>
	<dd>
		<spring:theme code="text.page.mybooking.bookingreference" arguments="${myBooking.code}" />
	</dd>
</dl>

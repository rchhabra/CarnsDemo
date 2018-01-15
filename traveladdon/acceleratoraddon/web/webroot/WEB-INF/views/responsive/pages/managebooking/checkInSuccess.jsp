<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<spring:htmlEscape defaultHtmlEscape="true" />

<div class="row heading-with-button">
	<div class="col-xs-12 col-sm-8">
		<h2 class="h2">
			<spring:theme code="checkin.success.header" text="Check In Success" />
		</h2>
	</div>
	<div class="col-xs-12 col-sm-4 button-inline">
		<c:url var="bookingDetailsUrl" value="/manage-booking/booking-details/${bookingReference}" />
		<a class="btn btn-primary btn-block" href="${fn:escapeXml(bookingDetailsUrl)}">
			<spring:theme code="checkin.success.back.to.booking.details" text="Back to Booking Details" />
		</a>
	</div>
</div>

<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="accommodationBooking" tagdir="/WEB-INF/tags/addons/accommodationaddon/responsive/accommodationbooking"%>
<%@ taglib prefix="bookingDetails" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/bookingdetails"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<spring:htmlEscape defaultHtmlEscape="true" />

<c:choose>
	<c:when test="${empty pageNotAvailable}">
		<div class="row">
			<div class="panel button-wrap">
				<div class="col-xs-12 col-sm-6 col-lg-8">
					<dl class="booking status">
						<dt>
							<spring:theme code="text.page.managemybooking.bookingstatus" text="Booking Status:" />
						</dt>
						<dd>
							<bookingDetails:status code="${accommodationReservationData.bookingStatusCode}" name="${accommodationReservationData.bookingStatusName}" />
						</dd>
					</dl>
				</div>
				<div class="col-xs-12 col-sm-6 col-lg-4">
					<input type="hidden" value="${fn:escapeXml(accommodationReservationData.code)}" name="bookingReference" id="bookingReference">
					<accommodationBooking:accommodationBookingAction bookingActionResponseData="${bookingActionResponse}" actionType="CANCEL_BOOKING" />
				</div>
			</div>
		</div>
		<bookingDetails:accommodationBookingDetails accommodationReservationData="${accommodationReservationData}" customerReviews="${customerReviews}" />

		<div class="y_cancelBookingConfirm"></div>
	</c:when>
	<c:otherwise>
		<div class="alert alert-danger " role="alert">
			<p>
				<spring:theme code="${pageNotAvailable}" />
			</p>
		</div>
	</c:otherwise>
</c:choose>


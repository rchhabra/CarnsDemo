<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="booking" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/bookingdetails"%>
<%@ taglib prefix="commonsBookingDetails" tagdir="/WEB-INF/tags/addons/travelcommons/responsive/bookingdetails"%>
<%@ taglib prefix="bookingDetails" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/bookingdetails"%>
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
							<bookingDetails:status code="${globalReservationData.bookingStatusCode}" name="${globalReservationData.bookingStatusName}" />
						</dd>
					</dl>
				</div>
				<div class="col-xs-12 col-sm-6 col-lg-4">
					<input type="hidden" value="${fn:escapeXml(bookingReference)}" name="bookingReference" id="bookingReference">
					<commonsBookingDetails:globalBookingAction bookingActionResponseData="${bookingActionResponse}" actionType="CANCEL_BOOKING" />
				</div>
			</div>
		</div>
		<c:if test="${globalReservationData.accommodationReservationData != null}">
			<bookingDetails:accommodationBookingDetails accommodationReservationData="${globalReservationData.accommodationReservationData}" isTravelSite="true" customerReviews="${customerReviews}" />
		</c:if>
		<c:if test="${globalReservationData.reservationData != null}">
			<bookingDetails:transportBookingDetails reservationData="${globalReservationData.reservationData}" />
		</c:if>
		<c:if test="${globalReservationData.reservationData.bookingStatusCode != 'ACTIVE_DISRUPTED_PENDING'}">
			<div class="fieldset">
				<div class="row">
					<div class="col-xs-12 col-sm-offset-4 col-sm-4">
						<c:if test="${globalReservationData.accommodationReservationData != null}">
							<c:set var="actionName">
								<spring:theme code="button.booking.details.accommodation.booking.action.cancel.transport.booking" text="Cancel Flight Booking" />
							</c:set>
							<booking:bookingaction bookingActionResponseData="${bookingActionResponse}" actionType="CANCEL_TRANSPORT_BOOKING" actionName="${actionName}" />
						</c:if>
					</div>
					<div class="col-xs-12 col-sm-4">
						<booking:bookingaction bookingActionResponseData="${bookingActionResponse}" actionType="AMEND_ANCILLARY" />
					</div>
				</div>
			</div>
		</c:if>
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
<commonsBookingDetails:addRoomToPackageErrorModal/>
<div class="modal transparent fade" id="y_addRoomModal" tabindex="-1" role="dialog" aria-hidden="true"></div>

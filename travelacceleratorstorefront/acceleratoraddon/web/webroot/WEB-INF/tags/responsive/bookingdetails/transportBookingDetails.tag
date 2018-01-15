<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="booking" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/bookingdetails"%>
<%@ taglib prefix="cancel" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/cancel"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/format"%>
<%@ attribute name="reservationData" required="true" type="de.hybris.platform.commercefacades.travel.reservation.data.ReservationData"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<%-- MANAGE BOOKING COMPONENT --%>

<div class="panel-body collapse in ${reservationData.bookingStatusCode == 'CANCELLED' ? 'booking-cancel' : ''}" id="booking-details-body">
	<div class="row">
		<c:if test="${reservationData.bookingStatusCode == 'ACTIVE_DISRUPTED_PENDING'}">
			<div class="alert-wrap">
				<div class="alert alert-danger" role="alert">
					<div class="row">
						<p>
							<strong><spring:theme code="text.page.managemybooking.bookingdetails.disruption"
									text="Your flight has been changed due to disruption" /></strong>
						</p>
						<p>
							<spring:theme code="text.page.managemybooking.bookingdetails.disruption.info1"
								text="Please see your new booking below with your original flight highlighted in red" />
						</p>
						<p>
							<spring:theme code="text.page.managemybooking.bookingdetails.disruption.info2"
								text="Do you wish to accept these changes? If you choose not to your booking will be cancelled and you will be refunded." />
						</p>
					</div>
					<div class="row">
						<div class="panel button-wrap">
							<input type="hidden" value="${fn:escapeXml(reservationData.code)}" name="bookingReference" id="bookingReference">
							<div class="col-xs-12 col-sm-5 col-md-4">
								<booking:bookingaction bookingActionResponseData="${bookingActionResponse}" actionType="ACCEPT_BOOKING" />
							</div>
							<div class="col-xs-12 col-sm-5 col-md-4">
								<booking:bookingaction bookingActionResponseData="${bookingActionResponse}" actionType="REJECT_BOOKING" />
							</div>
						</div>
					</div>
				</div>
			</div>
		</c:if>
	</div>
	<c:forEach var="reservationItem" items="${reservationData.reservationItems}">
		<div class="journey-wrapper">
			<booking:bookingitem reservationData="${reservationData}" reservationItem="${reservationItem}" cssClass="panel panel-default my-account-secondary-panel" />
		</div>
	</c:forEach>
	<booking:globalancillaries reservationData="${reservationData}" />
</div>
<div class="y_cancelTravellerConfirm"></div>

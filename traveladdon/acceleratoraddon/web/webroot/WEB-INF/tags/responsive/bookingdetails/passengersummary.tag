<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="booking" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/bookingdetails"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ attribute name="reservationItem" required="true" type="de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData"%>
<%@ attribute name="reservationData" required="true" type="de.hybris.platform.commercefacades.travel.reservation.data.ReservationData"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:url var="checkInUrl" value="/manage-booking/check-in/${reservationData.code}/${reservationItem.originDestinationRefNumber}" />
<div class="panel panel-default my-account-secondary-panel collapse in y_passenger_summary_collapse" id="passenger-summary-section-${reservationItem.originDestinationRefNumber}">
	<div class="panel-heading divided">
		<h3 class="panel-title">
			<spring:theme code="text.page.managemybooking.reservationitem.passengersummary" text="Passenger Summary" />
		</h3>
	</div>
	<div class="panel-body">
		<form>
			<fieldset class="fieldset ${fn:length(itineraries[0].travellers) gt 9 ? 'group-travellers' : ''}">
				<div class="row button-row" style="margin-bottom: 20px;">
					<div class="col-xs-12 col-sm-6 col-sm-offset-6">
						<div class="form-group col-xs-12">
							<booking:bookingaction bookingActionResponseData="${bookingActionResponse}" actionType="CHECK_IN_ALL" leg="${reservationItem.originDestinationRefNumber}" />
						</div>
					</div>
				</div>
				<%-- Passenger Row --%>
				<c:choose>
					<c:when test="${fn:length(reservationItem.reservationItinerary.travellers) gt 9}">
						<booking:groupPassengerInformation reservationData="${reservationData}" reservationItem="${reservationItem}" />
					</c:when>
					<c:otherwise>
						<booking:passengerinformation reservationItem="${reservationItem}" />
					</c:otherwise>
				</c:choose>
				<%-- Passenger Row --%>
			</fieldset>
		</form>
	</div>
</div>

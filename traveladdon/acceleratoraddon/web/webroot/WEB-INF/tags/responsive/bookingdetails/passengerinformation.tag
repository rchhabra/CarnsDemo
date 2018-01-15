<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="travellerdetails" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/travellerdetails"%>
<%@ taglib prefix="booking" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/bookingdetails"%>
<%@ attribute name="reservationItem" required="true" type="de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<c:forEach var="travellerData" items="${reservationItem.reservationItinerary.travellers}" >
	<c:url var="checkInUrl" value="/manage-booking/check-in/${reservationData.code}/${reservationItem.originDestinationRefNumber}?travellerReference=${travellerData.uid}" />
	<div class="row passenger-row">
		<div class="col-xs-12 name-row">
			<dl>
				<dt class="sr-only">
					<spring:theme code="text.page.managemybooking.reservationitem.passengersummary.passengerinformation.passengername" text="Passenger Name:" />
				</dt>
				<dd>${fn:escapeXml(travellerData.travellerInfo.firstName.concat(" ").concat(travellerData.travellerInfo.surname))}</dd>
				<c:if test="${additionalSecurity}">
					<dt class="sr-only">
						<spring:theme code="text.page.managemybooking.reservationitem.passengersummary.passengerinformation.passenger.reference" text="Passenger Reference:" />
					</dt>
					<dd class="passenger-reference"><span>
						<spring:theme code="text.page.managemybooking.reservationitem.passengersummary.passengerinformation.reference" text="Passenger Reference:" /></span> ${fn:escapeXml(travellerData.simpleUID)}</dd>
				</c:if>
			</dl>
		</div>
		<div class="col-xs-12 col-sm-6">
			<div class="col-xs-12 col-sm-6">
				<dl>
					<dt class="sr-only">
						<spring:theme code="text.page.managemybooking.reservationitem.passengersummary.passengerinformation.class" text="Class:" />
					</dt>
					<dd>${fn:escapeXml(reservationItem.reservationPricingInfo.itineraryPricingInfo.bundleTypeName)}</dd>
					<dt class="sr-only">
						<spring:theme code="text.page.managemybooking.reservationitem.passengersummary.passengerinformation.specialassistance" text="Special Assistance:" />
					</dt>
					<dd>
						<c:forEach var="specialServiceRequestData" items="${travellerData.specialRequestDetail.specialServiceRequests}" varStatus="status">
							${fn:escapeXml(specialServiceRequestData.name)}
							<c:if test="${not status.last}">
								,&nbsp;
							</c:if>
						</c:forEach>
					</dd>
				</dl>
			</div>
			<div class="col-xs-12 col-sm-6">
				<dl>
					<c:if test="${fn:length(reservationItem.reservationItinerary.originDestinationOptions[0].transportOfferings) gt 1}">
						<c:forEach items="${reservationItem.reservationItinerary.originDestinationOptions[0].transportOfferings}" var="routeTransportOffering" varStatus="idx">
							<c:if test="${idx.first}">
								<c:set var="originCode" value="${routeTransportOffering.sector.origin.code}" />
							</c:if>
							<c:if test="${idx.last}">
								<c:set var="routeOriginDestinationCode" value="${originCode} - ${routeTransportOffering.sector.destination.code}" />
							</c:if>
						</c:forEach>
						<travellerdetails:routeAncillariesInfo routeOriginDestinationCode="${routeOriginDestinationCode}" reservationItem="${reservationItem}" travellerCode="${travellerData.label}" />
					</c:if>

					<c:forEach items="${reservationItem.reservationItinerary.originDestinationOptions[0].transportOfferings}" var="transportOffering">
						<c:set var="sectorOriginDestinationCode" value="${transportOffering.sector.origin.code} - ${transportOffering.sector.destination.code}" />
						<travellerdetails:sectorAncillariesInfo sectorOriginDestinationCode="${sectorOriginDestinationCode}" reservationItem="${reservationItem}" travellerCode="${travellerData.label}" />
					</c:forEach>
				</dl>
			</div>

		</div>
		<div class="col-xs-12 col-sm-6 button-block">
			<div class="form-group col-xs-12">
				<booking:bookingaction bookingActionResponseData="${bookingActionResponse}" actionType="CHECK_IN" travellerUid="${travellerData.uid}" leg="${reservationItem.originDestinationRefNumber}" />
			</div>
			<div class="form-group col-xs-12">
				<input type="hidden" value="${fn:escapeXml(travellerData.uid)}" name="travellerUid" id="travellerUid">
				<input type="hidden" value="${fn:escapeXml(reservationData.code)}" name="bookingReference" id="bookingReference">
				<booking:bookingaction bookingActionResponseData="${bookingActionResponse}" actionType="REMOVE_TRAVELLER" travellerUid="${travellerData.uid}" leg="${reservationItem.originDestinationRefNumber}" />
			</div>
		</div>
	</div>
</c:forEach>

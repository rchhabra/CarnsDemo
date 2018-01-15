<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="booking" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/booking"%>
<%@ attribute name="reservationItem" required="true" type="de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<%-- display route information --%>
<c:set var="transportOfferings" value="${reservationItem.reservationItinerary.originDestinationOptions[0].transportOfferings}" />
<c:set var="originSector" value="${transportOfferings[0].sector}" />
<c:set var="finalSector" value="${transportOfferings[fn:length(transportOfferings) - 1].sector}" />
<h3 class="divided title">${fn:escapeXml(originSector.origin.name)}
	(${fn:escapeXml(originSector.origin.code)})
	<span class="spacer"></span>
	<span aria-hidden="true" class="glyphicon glyphicon-arrow-right"></span>
	${fn:escapeXml(finalSector.destination.name)} (${fn:escapeXml(finalSector.destination.code)}) - ${fn:escapeXml(reservationItem.reservationPricingInfo.itineraryPricingInfo.bundleTypeName)}
</h3>
<%-- display flight information --%>
<div class="row">
	<div class="col-xs-12">
		<div class="panel-body collapse in" id="section-${fn:escapeXml(reservationItem.originDestinationRefNumber)}">
			<booking:journeyDetails transportOfferings="${transportOfferings}" />
			<c:if test="${not empty disruptedReservation}">
				<c:forEach var="disruptedItem" items="${disruptedReservation.reservationItems}">
					<c:if test="${disruptedItem.originDestinationRefNumber == reservationItem.originDestinationRefNumber}">
						<div class="alert-wrap">
							<div class="alert alert-danger" role="alert">
								<h3 class="panel-title">
									<spring:theme code="text.page.managemybooking.bookingdetails.originalflight" text="Original Flight" />
								</h3>
								<booking:journeyDetails transportOfferings="${disruptedItem.reservationItinerary.originDestinationOptions[0].transportOfferings}" />
							</div>
						</div>
					</c:if>
				</c:forEach>
			</c:if>
		</div>
	</div>
</div>

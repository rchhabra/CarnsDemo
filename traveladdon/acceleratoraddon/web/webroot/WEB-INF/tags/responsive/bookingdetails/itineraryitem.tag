<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="booking" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/bookingdetails"%>
<%@ attribute name="reservationItem" required="true" type="de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData"%>
<%@ attribute name="cssClass" required="true" type="java.lang.String"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="${fn:escapeXml(cssClass)}">
	<%-- display route information --%>
	<div class="panel-heading">
		<c:set var="transportOfferings" value="${reservationItem.reservationItinerary.originDestinationOptions[0].transportOfferings}" />
		<c:set var="originSector" value="${transportOfferings[0].sector}" />
		<c:set var="finalSector" value="${transportOfferings[fn:length(transportOfferings) - 1].sector}" />
		<h3 class="panel-title title-collapse">${fn:escapeXml(originSector.origin.name)} (${fn:escapeXml(originSector.origin.code)})
			<span aria-hidden="true" class="glyphicon glyphicon-arrow-right"></span>
			${fn:escapeXml(finalSector.destination.name)} (${fn:escapeXml(finalSector.destination.code)}) -  ${fn:escapeXml(reservationItem.reservationPricingInfo.itineraryPricingInfo.bundleTypeName)}
		</h3>
	</div>
	<%-- display flight information --%>
	<div class="panel-body collapse in" id="section-${fn:escapeXml(reservationItem.originDestinationRefNumber)}">
		<booking:journeyDetails transportOfferings="${transportOfferings}"/>
		<c:if test="${not empty disruptedReservation}">
			<c:forEach var="disruptedItem" items="${disruptedReservation.reservationItems}">
				<c:if test="${disruptedItem.originDestinationRefNumber == reservationItem.originDestinationRefNumber}">
					<div class="alert-wrap">
						<div class="alert alert-danger" role="alert">
							<h3 class="panel-title"><spring:theme code="text.page.managemybooking.bookingdetails.originalflight" text="Original Flight" /></h3>
							<booking:journeyDetails transportOfferings="${disruptedItem.reservationItinerary.originDestinationOptions[0].transportOfferings}"/>
						</div>
					</div>
				</c:if>
			</c:forEach>
		</c:if>
	</div>
</div>

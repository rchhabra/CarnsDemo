<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ attribute name="reservationItem" required="true" type="de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData"%>
<%@ attribute name="travellerCode" required="true" type="java.lang.String"%>
<%@ attribute name="sectorOriginDestinationCode" required="true" type="java.lang.String"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:set var="displaySeat" value="${false}" />

<c:forEach var="originDestinationOfferInfo" items="${reservationItem.reservationPricingInfo.originDestinationOfferInfos}">
	<c:if test="${fn:length(originDestinationOfferInfo.transportOfferings) eq 1}">
		<c:set var="originDestinationCode" value="${originDestinationOfferInfo.transportOfferings[0].sector.origin.code} - ${originDestinationOfferInfo.transportOfferings[0].sector.destination.code}" />
		<c:if test="${sectorOriginDestinationCode eq originDestinationCode}">
			<c:set var="showRouteCode" value="true" />
			<ul class="list-unstyled ${fn:length(reservationItem.reservationItinerary.travellers) gt 9 ? 'col-xs-6' : ''}">
				<c:forEach var="offerPricingInfo" items="${originDestinationOfferInfo.offerPricingInfos}">
					<c:forEach var="travellerBreakdown" items="${offerPricingInfo.travellerBreakdowns}">
						<c:if test="${travellerBreakdown.traveller.label eq travellerCode}">
							<c:set var="displaySeat" value="${true}" />
							<c:if test="${showRouteCode}">
								<li>
									<strong>${fn:escapeXml(sectorOriginDestinationCode)}</strong>
								</li>
								<c:set var="showRouteCode" value="false" />
							</c:if>
							<c:set var="includedProductText">
								<spring:theme code="text.page.managemybooking.bookingdetails.included.product" text="This product is included in your bundle." />
							</c:set>
							<c:choose>
								<c:when test="${offerPricingInfo.bundleIndicator == 0}">
									<li>${fn:escapeXml(travellerBreakdown.quantity)}&nbsp;x&nbsp;${fn:escapeXml(offerPricingInfo.product.name)}</li>
								</c:when>
								<c:otherwise>
								<li>
									<a tabindex="0" class="included-product" role="button" data-toggle="popover" data-trigger="focus" data-placement="bottom" data-container="body" data-content="${fn:escapeXml(includedProductText)}">${fn:escapeXml(travellerBreakdown.quantity)}&nbsp;x&nbsp;${fn:escapeXml(offerPricingInfo.product.name)}&nbsp;
										<i class="glyphicon glyphicon-info-sign"></i>
									</a>
								</li>
								</c:otherwise>
							</c:choose>
						</c:if>
					</c:forEach>
				</c:forEach>
				<c:if test="${displaySeat}">
					<c:forEach items="${reservationItem.reservationPricingInfo.selectedSeats}" var="selectedSeat">
						<c:if test="${selectedSeat.traveller.label eq travellerCode}">
							<c:set var="seatOriginDestinationCode" value="${selectedSeat.transportOffering.sector.origin.code} - ${selectedSeat.transportOffering.sector.destination.code}" />
							<c:if test="${sectorOriginDestinationCode eq seatOriginDestinationCode}">
								<li>
									<spring:theme code="text.page.travellerdetails.ancillaries.seat" text="Seat:" />&nbsp;${fn:escapeXml(selectedSeat.seatNumber)}</li>
							</c:if>
						</c:if>
					</c:forEach>
				</c:if>
			</ul>
		</c:if>
	</c:if>
</c:forEach>
<c:if test="${not displaySeat}">
	<c:forEach items="${reservationItem.reservationPricingInfo.selectedSeats}" var="selectedSeat">
		<c:if test="${selectedSeat.traveller.label eq travellerCode}">
			<c:set var="seatOriginDestinationCode" value="${selectedSeat.transportOffering.sector.origin.code} - ${selectedSeat.transportOffering.sector.destination.code}" />
			<c:if test="${sectorOriginDestinationCode eq seatOriginDestinationCode}">
				<ul class="list-unstyled">
					<li>
						<strong>${fn:escapeXml(sectorOriginDestinationCode)}</strong>
					</li>
					<li>
						<spring:theme code="text.page.travellerdetails.ancillaries.seat" text="Seat:" />&nbsp;${fn:escapeXml(selectedSeat.seatNumber)}
					</li>
				</ul>
			</c:if>
		</c:if>
	</c:forEach>
</c:if>

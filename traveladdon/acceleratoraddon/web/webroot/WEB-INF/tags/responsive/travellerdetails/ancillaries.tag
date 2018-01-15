<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="travellerdetails" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/travellerdetails"%>

<%@ attribute name="reservationData" required="true" type="de.hybris.platform.commercefacades.travel.reservation.data.ReservationData"%>
<%@ attribute name="travellerCode" required="true" type="java.lang.String"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<c:set var="displayAncillaryData" value="false" />

<%-- check is traveller has added any ancillaries --%>
<c:forEach items="${reservationData.reservationItems}" var="reservationItem">
	<c:forEach items="${reservationItem.reservationPricingInfo.originDestinationOfferInfos}" var="originDestinationOfferInfo">
		<c:forEach items="${originDestinationOfferInfo.offerPricingInfos}" var="offerPricingInfos">
			<c:forEach items="${offerPricingInfos.travellerBreakdowns}" var="travellerBreakdown">
				<c:if test="${travellerBreakdown.traveller.label eq travellerCode}">
					<c:set var="displayAncillaryData" value="true" />
				</c:if>
			</c:forEach>
		</c:forEach>
	</c:forEach>
</c:forEach>

<%-- check global ancillaries --%>
<c:set var="gaItems" value="" />
<c:forEach var="offerPricingInfo" items="${reservationData.offerPricingInfos}">
	<c:forEach var="travellerBreakdown" items="${offerPricingInfo.travellerBreakdowns}">
		<c:if test="${travellerBreakdown.traveller.label eq travellerCode}">
			<c:set var="displayAncillaryData" value="true" />
			<c:set var="gaItem" value="${travellerBreakdown.quantity}&nbsp;x&nbsp;${offerPricingInfo.product.name}" />
			<c:choose>
				<c:when test="${empty gaItems}">
					<c:set var="gaItems" value="${gaItem}" />
				</c:when>
				<c:otherwise>
					<c:set var="gaItems" value="${gaItems}, ${gaItem}" />
				</c:otherwise>
			</c:choose>
		</c:if>
	</c:forEach>
</c:forEach>

<%-- check is traveller has added a seat --%>
<c:if test="${not displayAncillaryData}">
	<c:forEach items="${reservationData.reservationItems}" var="reservationItem">
		<c:forEach items="${reservationItem.reservationPricingInfo.selectedSeats}" var="selectedSeats">
			<c:if test="${selectedSeats.traveller.label eq travellerCode}">
				<c:set var="displayAncillaryData" value="true" />
			</c:if>
		</c:forEach>
	</c:forEach>
</c:if>
<c:if test="${displayAncillaryData}">
	<div class="${fn:length(reservationData.reservationItems[0].reservationItinerary.travellers) gt 9 ? 'col-xs-12' : ''}">
		<div id="ancillary-info" class="${fn:length(reservationData.reservationItems[0].reservationItinerary.travellers) le 9 ? 'summary' : ''} traveller-details clearfix">
			<h4>
				<spring:theme code="text.page.travellerdetails.ancillaries.heading.ancillariessummary" text="Ancillaries Summary" />
			</h4>
			<div class="col-xs-12">
				<div class="row">
					<c:forEach var="reservationItem" items="${reservationData.reservationItems}">
						<c:if test="${fn:length(reservationItem.reservationItinerary.originDestinationOptions[0].transportOfferings) gt 1}">
							<c:forEach items="${reservationItem.reservationItinerary.originDestinationOptions[0].transportOfferings}" var="routeTransportOffering" varStatus="idx">
								<c:if test="${idx.first}">
									<c:set var="originCode" value="${routeTransportOffering.sector.origin.code}" />
								</c:if>
								<c:if test="${idx.last}">
									<c:set var="routeOriginDestinationCode" value="${originCode} - ${routeTransportOffering.sector.destination.code}" />
								</c:if>
							</c:forEach>
							<travellerdetails:routeAncillariesInfo routeOriginDestinationCode="${routeOriginDestinationCode}" reservationItem="${reservationItem}" travellerCode="${travellerCode}" />
						</c:if>
						<c:forEach items="${reservationItem.reservationItinerary.originDestinationOptions[0].transportOfferings}" var="transportOffering">
							<c:set var="sectorOriginDestinationCode" value="${transportOffering.sector.origin.code} - ${transportOffering.sector.destination.code}" />
							<travellerdetails:sectorAncillariesInfo sectorOriginDestinationCode="${sectorOriginDestinationCode}" reservationItem="${reservationItem}" travellerCode="${travellerCode}" />
						</c:forEach>
					</c:forEach>
					<c:if test="${not empty gaItems}">
						<ul class="list-unstyled">
							<li><strong><spring:theme code="text.page.travellerdetails.ancillaries.heading.globalancillaries" text="Additional Products" /></strong></li>
							<li>${fn:escapeXml(gaItems)}</li>
						</ul>
					</c:if>
				</div>
			</div>
		</div>
	</div>
</c:if>

<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="packageDetails" tagdir="/WEB-INF/tags/addons/travelcommons/responsive/packagedetails"%>
<%@ attribute name="transportPackageResponse" required="true" type="de.hybris.platform.commercefacades.packages.response.TransportPackageResponseData"%>
<div class="panel-travel-details">
	<c:set var="reservationData" value="${transportPackageResponse.reservationData}" />
	<c:if test="${not empty reservationData.reservationItems}">
		<c:forEach var="reservationItem" items="${reservationData.reservationItems}" varStatus="idx">
			<packageDetails:selectedTransportOfferings itinerary="${reservationItem.reservationItinerary}" itineraryPricingInfo="${reservationItem.reservationPricingInfo.itineraryPricingInfo}" pricedItineraryIndex="${idx.index}" />
		</c:forEach>
	</c:if>
</div>

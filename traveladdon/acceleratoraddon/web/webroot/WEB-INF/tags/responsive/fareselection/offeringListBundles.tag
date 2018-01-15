<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ attribute name="bundleTypes" required="true" type="java.lang.String"%>
<%@ attribute name="pricedItinerary" required="true" type="de.hybris.platform.commercefacades.travel.PricedItineraryData"%>
<%@ attribute name="refNumber" required="true" type="java.lang.Integer"%>
<%@ attribute name="name" required="true" type="java.lang.String"%>
<%@ taglib prefix="fareselection" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/fareselection"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<legend class="sr-only">
	<spring:theme code="sr.fareselection.travelclass" />
</legend>

<c:choose>
	<c:when test="${refNumber == outboundRefNumber}">
		<c:set var="pricedItineraryDateTime" value="${pricedItinerary.itinerary.originDestinationOptions[0].transportOfferings[fn:length(pricedItinerary.itinerary.originDestinationOptions[0].transportOfferings) - 1].arrivalTime}" />
	</c:when>
	<c:otherwise>
		<c:set var="pricedItineraryDateTime" value="${pricedItinerary.itinerary.originDestinationOptions[0].transportOfferings[0].departureTime}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${bundleTypes == 'STANDARD'}">
		<c:set var="ecoItineraryPricingInfo" value="" />
		<c:set var="ecoPlusItineraryPricingInfo" value="" />
		<c:set var="businessItineraryPricingInfo" value="" />
		<c:forEach items="${pricedItinerary.itineraryPricingInfos}" var="itineraryPricingInfo" varStatus="pricingIdx">
			<c:if test="${itineraryPricingInfo.bundleType eq ecoBundleType}">
				<c:set var="ecoItineraryPricingInfo" value="${itineraryPricingInfo}" />
			</c:if>
			<c:if test="${itineraryPricingInfo.bundleType eq ecoPlusBundleType}">
				<c:set var="ecoPlusItineraryPricingInfo" value="${itineraryPricingInfo}" />
			</c:if>
			<c:if test="${itineraryPricingInfo.bundleType eq businessBundleType}">
				<c:set var="businessItineraryPricingInfo" value="${itineraryPricingInfo}" />
			</c:if>
		</c:forEach>
		<fareselection:itineraryPricingInfo itineraryPricingInfo="${ecoItineraryPricingInfo}" pricedItinerary="${pricedItinerary}" pricedItineraryDateTime="${pricedItineraryDateTime}" name="${name}" />
		<fareselection:itineraryPricingInfo itineraryPricingInfo="${ecoPlusItineraryPricingInfo}" pricedItinerary="${pricedItinerary}" pricedItineraryDateTime="${pricedItineraryDateTime}" name="${name}" />
		<fareselection:itineraryPricingInfo itineraryPricingInfo="${businessItineraryPricingInfo}" pricedItinerary="${pricedItinerary}" pricedItineraryDateTime="${pricedItineraryDateTime}" name="${name}" />
	</c:when>
	<c:otherwise>
		<c:forEach items="${pricedItinerary.itineraryPricingInfos}" var="itineraryPricingInfo" varStatus="pricingIdx">
			<c:if test="${itineraryPricingInfo.bundleType != ecoBundleType && itineraryPricingInfo.bundleType != ecoPlusBundleType && itineraryPricingInfo.bundleType != businessBundleType}">
				<fareselection:itineraryPricingInfo itineraryPricingInfo="${itineraryPricingInfo}" pricedItinerary="${pricedItinerary}" pricedItineraryDateTime="${pricedItineraryDateTime}" name="${name}" />
			</c:if>
		</c:forEach>
	</c:otherwise>
</c:choose>

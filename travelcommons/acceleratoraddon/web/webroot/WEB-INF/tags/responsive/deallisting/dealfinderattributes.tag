<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="propertyData" required="true" type="de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData"%>
<%@ attribute name="transportData" required="true" type="de.hybris.platform.commercefacades.travel.FareSelectionData"%>
<%@ attribute name="returnRouteLocation" required="true" type="de.hybris.platform.commercefacades.travel.TransportFacilityData"%>
<%@ attribute name="departureRouteLocation" required="true" type="de.hybris.platform.commercefacades.travel.TransportFacilityData"%>
<%@ attribute name="departureDate" required="true" type="java.lang.String"%>
<%@ attribute name="returnDate" required="true" type="java.lang.String"%>
<%@ attribute name="departureLocationCity" required="true" type="java.lang.String"%>
<%@ attribute name="returnLocationCity" required="true" type="java.lang.String"%>

<div class="y_travelFinderAttributes">
	<input type="hidden" class="y_cabinClass" value="M" />
	<input type="hidden" class="y_arrivalLocation" value="${fn:escapeXml(returnRouteLocation.code)}" />
	<input type="hidden" class="y_departureLocation" value="${fn:escapeXml(departureRouteLocation.code)}" />
	<input type="hidden" class="y_departureLocationCityName" value="${fn:escapeXml(departureLocationCity)}">
	<input type="hidden" class="y_arrivalLocationCityName" value="${fn:escapeXml(returnLocationCity)}">
	<input type="hidden" class="y_arrivalLocationName" value="${fn:escapeXml(returnRouteLocation.name)}" />
	<input type="hidden" class="y_departureLocationName" value="${fn:escapeXml(departureRouteLocation.name)}" />
	<input type="hidden" class="y_arrivalLocationSuggestionType" value="AIRPORTGROUP" />
	<input type="hidden" class="y_departureLocationSuggestionType" value="AIRPORTGROUP" />
	<input type="hidden" class="y_departingDateTime" value="${fn:escapeXml(departureDate)}" />
	<input type="hidden" class="y_returnDateTime" value="${fn:escapeXml(returnDate)}" />
	<input type="hidden" class="y_destinationLocation" value="${fn:escapeXml(propertyData.accommodationReference.accommodationOfferingCode)}" />
	<input type="hidden" class="y_destinationLocationName" value="${fn:escapeXml(propertyData.accommodationReference.accommodationOfferingName)}" />
	<input type="hidden" class="y_suggestionType" value="PROPERTY" />
	<input type="hidden" class="y_numberOfRooms" value="1" />
	<c:forEach var="ptcFareBreakdownData" items="${transportData.pricedItineraries[0].itineraryPricingInfos[0].ptcFareBreakdownDatas}">
		<input type="hidden" class="y_roomStayCandidates_passengerType_${fn:escapeXml(ptcFareBreakdownData.passengerTypeQuantity.passengerType.code)}_quantity" value="${fn:escapeXml(ptcFareBreakdownData.passengerTypeQuantity.quantity)}" />
	</c:forEach>
</div>

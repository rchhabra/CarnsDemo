<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="packageDetails" tagdir="/WEB-INF/tags/addons/travelcommons/responsive/packagedetails"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ attribute name="transportPackageResponse" required="true" type="de.hybris.platform.commercefacades.packages.response.TransportPackageResponseData"%>
<div class="panel-travel-details">
    <c:set var="fareSelection" value="${transportPackageResponse.fareSearchResponse}"/>
    <c:if test="${not empty fareSelection.pricedItineraries}">
	    <c:forEach var="pricedItinerary" items="${fareSelection.pricedItineraries}" varStatus="idx">
		    <c:set var="itinerary" value="${pricedItinerary.itinerary}" />
		    <c:forEach var="itineraryPricingInfo" items="${pricedItinerary.itineraryPricingInfos}">
			    <c:if test="${itineraryPricingInfo.selected}">
				    <c:if test="${not empty isPackageInCart && !isPackageInCart}">
				        <packageDetails:transportationFormAttributes pricedItinerary="${pricedItinerary}" itineraryPricingInfo="${itineraryPricingInfo}" />
				    </c:if>
				    <packageDetails:selectedTransportOfferings itinerary="${itinerary}" itineraryPricingInfo="${itineraryPricingInfo}" pricedItineraryIndex="${idx.index}" originDestinationRefNum="${pricedItinerary.originDestinationRefNumber}"/>
			    </c:if>
		    </c:forEach>
	    </c:forEach>
    </c:if>
</div>

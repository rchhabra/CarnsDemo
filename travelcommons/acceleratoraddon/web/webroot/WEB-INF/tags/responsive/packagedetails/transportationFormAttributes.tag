<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="pricedItinerary" required="true" type="de.hybris.platform.commercefacades.travel.PricedItineraryData"%>
<%@ attribute name="itineraryPricingInfo" required="true" type="de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData"%>

<c:url var="addBundleToCartUrl" value="/cart/add-transport-bundle"/>
<form:form id="packageAddBundleToCartForm-${fn:escapeXml(pricedItinerary.originDestinationRefNumber)}" name="addBundleToCartForm" action="${addBundleToCartUrl}" method="POST" class="y_packageAddBundleToCartForm">

	<input type="hidden" readonly="readonly" name="travelRouteCode" value="${fn:escapeXml(pricedItinerary.itinerary.route.code)}" />
	<input type="hidden" readonly="readonly" name="originDestinationRefNumber" value="${fn:escapeXml(pricedItinerary.originDestinationRefNumber)}" />

	<input type="hidden" readonly="readonly" name="itineraryPricingInfo.bundleTypeName" value="${fn:escapeXml(itineraryPricingInfo.bundleTypeName)}" />
	<input type="hidden" readonly="readonly" name="itineraryPricingInfo.bundleType" value="${fn:escapeXml(itineraryPricingInfo.bundleType)}" />
	<input type="hidden" readonly="readonly" name="itineraryPricingInfo.itineraryIdentifier" value="${fn:escapeXml(itineraryPricingInfo.itineraryIdentifier)}" />

	<c:forEach var="entry" items="${itineraryPricingInfo.bundleTemplates}" varStatus="idx1">
		<input type="hidden" readonly="readonly" name="itineraryPricingInfo.bundleTemplates[${idx1.index}].fareProductBundleTemplateId" value="${fn:escapeXml(entry.fareProductBundleTemplateId)}" />
		<input type="hidden" readonly="readonly" name="itineraryPricingInfo.bundleTemplates[${idx1.index}].bundleType" value="${fn:escapeXml(entry.bundleType)}" />
		<c:forEach var="fareProductEntry" items="${entry.fareProducts}" varStatus="idx2">
			<input type="hidden" readonly="readonly" name="itineraryPricingInfo.bundleTemplates[${idx1.index}].fareProducts[${idx2.index}].code" value="${fn:escapeXml(fareProductEntry.code)}" />
			<input type="hidden" readonly="readonly" name="itineraryPricingInfo.bundleTemplates[${idx1.index}].fareProducts[${idx2.index}].bookingClass" value="${fn:escapeXml(fareProductEntry.bookingClass)}" />
			<input type="hidden" readonly="readonly" name="itineraryPricingInfo.bundleTemplates[${idx1.index}].fareProducts[${idx2.index}].fareBasisCode" value="${fn:escapeXml(fareProductEntry.fareBasisCode)}" />
		</c:forEach>
		<c:forEach var="transportOfferingEntry" items="${entry.transportOfferings}" varStatus="idx3">
			<input type="hidden" readonly="readonly" name="itineraryPricingInfo.bundleTemplates[${idx1.index}].transportOfferings[${idx3.index}].code" value="${fn:escapeXml(transportOfferingEntry.code)}" />
		</c:forEach>
	</c:forEach>

	<c:forEach var="entry" items="${itineraryPricingInfo.ptcFareBreakdownDatas}" varStatus="i">
		<input type="hidden" readonly="readonly" name="passengerTypeQuantityList[${i.index}].passengerType.code" value="${fn:escapeXml(entry.passengerTypeQuantity.passengerType.code)}" />
		<input type="hidden" readonly="readonly" name="passengerTypeQuantityList[${i.index}].passengerType.name" value="${fn:escapeXml(entry.passengerTypeQuantity.passengerType.name)}" />
		<input type="hidden" readonly="readonly" name="passengerTypeQuantityList[${i.index}].quantity" value="${fn:escapeXml(entry.passengerTypeQuantity.quantity)}" />
	</c:forEach>

</form:form>

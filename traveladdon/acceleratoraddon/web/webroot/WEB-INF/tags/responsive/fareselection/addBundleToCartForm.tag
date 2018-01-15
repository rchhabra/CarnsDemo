<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="itineraryPricingInfo" required="true" type="de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData"%>
<%@ attribute name="pricedItinerary" required="true" type="de.hybris.platform.commercefacades.travel.PricedItineraryData"%>

<c:url var="addToCartUrl" value="${addBundleToCartUrl}" />
<form:form modelAttribute="addBundleToCartForm" class="y_addBundleToCartForm" id="addBundleToCartForm-${fn:escapeXml(pricedItinerary.id)}-${fn:escapeXml(itineraryPricingInfo.bundleType)}" action="${fn:escapeXml(addToCartUrl)}" method="post">

	<form:input type="hidden" readonly="true" path="travelRouteCode" value="${fn:escapeXml(pricedItinerary.itinerary.route.code)}" />
	<form:input type="hidden" readonly="true" path="originDestinationRefNumber" value="${fn:escapeXml(pricedItinerary.originDestinationRefNumber)}" />

	<%-- passengerTypeQuantityDatas --%>
	<c:forEach var="entry" items="${itineraryPricingInfo.ptcFareBreakdownDatas}" varStatus="i">
		<form:input type="hidden" readonly="true" path="passengerTypeQuantityList[${fn:escapeXml(i.index)}].passengerType.code" value="${fn:escapeXml(entry.passengerTypeQuantity.passengerType.code)}" />
		<form:input type="hidden" readonly="true" path="passengerTypeQuantityList[${fn:escapeXml(i.index)}].passengerType.name" value="${fn:escapeXml(entry.passengerTypeQuantity.passengerType.name)}" />
		<form:input type="hidden" readonly="true" path="passengerTypeQuantityList[${fn:escapeXml(i.index)}].quantity" value="${fn:escapeXml(entry.passengerTypeQuantity.quantity)}" />
	</c:forEach>

	<form:input type="hidden" readonly="true" path="itineraryPricingInfo.bundleTypeName" value="${fn:escapeXml(itineraryPricingInfo.bundleTypeName)}" />
	<form:input type="hidden" readonly="true" path="itineraryPricingInfo.bundleType" value="${fn:escapeXml(itineraryPricingInfo.bundleType)}" />
	<form:input type="hidden" readonly="true" path="itineraryPricingInfo.itineraryIdentifier" value="${fn:escapeXml(itineraryPricingInfo.itineraryIdentifier)}" />

	<%-- itineraryPricingInfo --%>
	<c:forEach var="entry" items="${itineraryPricingInfo.bundleTemplates}" varStatus="btIdx">
		<form:input type="hidden" readonly="true" path="itineraryPricingInfo.bundleTemplates[${fn:escapeXml(btIdx.index)}].fareProductBundleTemplateId" value="${fn:escapeXml(entry.fareProductBundleTemplateId)}" />
		<form:input type="hidden" readonly="true" path="itineraryPricingInfo.bundleTemplates[${fn:escapeXml(btIdx.index)}].bundleType" value="${fn:escapeXml(entry.bundleType)}" />
		<c:forEach var="fareProductEntry" items="${entry.fareProducts}" varStatus="fpIdx">
			<form:input type="hidden" readonly="true" path="itineraryPricingInfo.bundleTemplates[${fn:escapeXml(btIdx.index)}].fareProducts[${fn:escapeXml(fpIdx.index)}].code" value="${fn:escapeXml(fareProductEntry.code)}" />
			<form:input type="hidden" readonly="true" path="itineraryPricingInfo.bundleTemplates[${fn:escapeXml(btIdx.index)}].fareProducts[${fn:escapeXml(fpIdx.index)}].bookingClass" value="${fn:escapeXml(fareProductEntry.bookingClass)}" />
			<form:input type="hidden" readonly="true" path="itineraryPricingInfo.bundleTemplates[${fn:escapeXml(btIdx.index)}].fareProducts[${fn:escapeXml(fpIdx.index)}].fareBasisCode" value="${fn:escapeXml(fareProductEntry.fareBasisCode)}" />
		</c:forEach>
		<c:forEach var="transportOfferingEntry" items="${entry.transportOfferings}" varStatus="toIdx">
			<form:input type="hidden" readonly="true" path="itineraryPricingInfo.bundleTemplates[${fn:escapeXml(btIdx.index)}].transportOfferings[${fn:escapeXml(toIdx.index)}].code" value="${fn:escapeXml(transportOfferingEntry.code)}" />
		</c:forEach>
	</c:forEach>
</form:form>

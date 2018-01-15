<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ attribute name="pricedItinerary" required="true" type="de.hybris.platform.commercefacades.travel.PricedItineraryData"%>
<%@ attribute name="pricedItineraryInfo" required="true" type="de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData"%>
<%@ attribute name="index" required="true" type="java.lang.Integer"%>

<div class="y_addBundle_form-${fn:escapeXml(pricedItinerary.originDestinationRefNumber)}">

	<form:input type="hidden" id="y_addBundleToCartFormRedirectUrl" path="redirectUrl" />
	
	<form:input type="hidden" readonly="true" path="travelRouteCodes[${fn:escapeXml(index)}]" value="${fn:escapeXml(pricedItinerary.itinerary.route.code)}" />
	<form:input type="hidden" readonly="true" path="originDestinationRefNumbers[${fn:escapeXml(index)}]" value="${fn:escapeXml(pricedItinerary.originDestinationRefNumber)}" />
	<form:input type="hidden" readonly="true" path="itineraryPricingInfos[${fn:escapeXml(index)}].bundleTypeName" value="${fn:escapeXml(pricedItineraryInfo.bundleTypeName)}" />
	<form:input type="hidden" readonly="true" path="itineraryPricingInfos[${fn:escapeXml(index)}].bundleType" value="${fn:escapeXml(pricedItineraryInfo.bundleType)}" />

	<c:forEach var="entry" items="${pricedItineraryInfo.bundleTemplates}" varStatus="idx1">
		<form:input type="hidden" readonly="true" path="itineraryPricingInfos[${fn:escapeXml(index)}].bundleTemplates[${fn:escapeXml(idx1.index)}].fareProductBundleTemplateId" value="${fn:escapeXml(entry.fareProductBundleTemplateId)}" />
		<form:input type="hidden" readonly="true" path="itineraryPricingInfos[${fn:escapeXml(index)}].bundleTemplates[${fn:escapeXml(idx1.index)}].bundleType" value="${fn:escapeXml(entry.bundleType)}" />
		<c:forEach var="fareProductEntry" items="${entry.fareProducts}" varStatus="idx2">
			<form:input type="hidden" readonly="true" path="itineraryPricingInfos[${fn:escapeXml(index)}].bundleTemplates[${fn:escapeXml(idx1.index)}].fareProducts[${fn:escapeXml(idx2.index)}].code" value="${fn:escapeXml(fareProductEntry.code)}" />
			<form:input type="hidden" readonly="true" path="itineraryPricingInfos[${fn:escapeXml(index)}].bundleTemplates[${fn:escapeXml(idx1.index)}].fareProducts[${fn:escapeXml(idx2.index)}].bookingClass" value="${fn:escapeXml(fareProductEntry.bookingClass)}" />
			<form:input type="hidden" readonly="true" path="itineraryPricingInfos[${fn:escapeXml(index)}].bundleTemplates[${fn:escapeXml(idx1.index)}].fareProducts[${fn:escapeXml(idx2.index)}].fareBasisCode" value="${fn:escapeXml(fareProductEntry.fareBasisCode)}" />
		</c:forEach>
		<c:forEach var="transportOfferingEntry" items="${entry.transportOfferings}" varStatus="idx3">
			<form:input type="hidden" readonly="true" path="itineraryPricingInfos[${fn:escapeXml(index)}].bundleTemplates[${fn:escapeXml(idx1.index)}].transportOfferings[${fn:escapeXml(idx3.index)}].code" value="${fn:escapeXml(transportOfferingEntry.code)}" />
		</c:forEach>
	</c:forEach>
	
</div>

<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="deallisting" tagdir="/WEB-INF/tags/addons/travelcommons/responsive/deallisting"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="dealBundleTemplateId" required="true" type="java.lang.String"%>
<%@ attribute name="departureDate" required="true" type="java.lang.String"%>
<%@ attribute name="returnDate" required="true" type="java.lang.String"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<c:set var="itineraries" value="${packageResponseData.transportPackageResponse.fareSearchResponse.pricedItineraries}" />
<c:url var="submitUrl" value="/cart/addDeal" />
<form:form action="${submitUrl}" modelAttribute="addDealToCartForm" method="POST" class="y_addDealToCartForm">
	<form:input type="hidden" path="bundleTemplateID" value="${fn:escapeXml(dealBundleTemplateId)}" />
	<c:forEach var="itinerary" items="${itineraries}" varStatus="itineraryID">
		<c:forEach var="entry" items="${itinerary.itineraryPricingInfos[0].bundleTemplates}" varStatus="templateID">
			<form:input type="hidden" readonly="true" path="itineraryPricingInfos[${itineraryID.index}].itineraryIdentifier" value="${fn:escapeXml(itinerary.itineraryPricingInfos[0].itineraryIdentifier)}" />
			<c:forEach var="fareProductEntry" items="${entry.fareProducts}" varStatus="fareProdID">
				<form:input type="hidden" readonly="true" path="itineraryPricingInfos[${itineraryID.index}].bundleTemplates[${templateID.index}].fareProducts[${fareProdID.index}].code" value="${fn:escapeXml(fareProductEntry.code)}" />
				<form:input type="hidden" readonly="true" path="itineraryPricingInfos[${itineraryID.index}].bundleTemplates[${templateID.index}].fareProducts[${fareProdID.index}].bookingClass" value="${fn:escapeXml(fareProductEntry.bookingClass)}" />
				<form:input type="hidden" readonly="true" path="itineraryPricingInfos[${itineraryID.index}].bundleTemplates[${templateID.index}].fareProducts[${fareProdID.index}].fareBasisCode" value="${fn:escapeXml(fareProductEntry.fareBasisCode)}" />
			</c:forEach>
			<c:forEach var="trOffEntry" items="${entry.transportOfferings}" varStatus="trOffID">
				<form:input type="hidden" readonly="true" path="itineraryPricingInfos[${itineraryID.index}].bundleTemplates[${templateID.index}].transportOfferings[${trOffID.index}].code" value="${fn:escapeXml(trOffEntry.code)}" />
				<form:input type="hidden" readonly="true" path="itineraryPricingInfos[${itineraryID.index}].bundleTemplates[${templateID.index}].transportOfferings[${trOffID.index}].number" value="${fn:escapeXml(trOffEntry.travelProvider.code)}${fn:escapeXml(trOffEntry.number)}" />
			</c:forEach>
		</c:forEach>
	</c:forEach>
	<c:forEach var="entry" items="${itineraries[0].itineraryPricingInfos[0].ptcFareBreakdownDatas}" varStatus="i">
		<form:input path="passengerTypes[${i.index}].passengerType.code" type="hidden" readonly="true" value="${fn:escapeXml(entry.passengerTypeQuantity.passengerType.code)}" />
		<form:input path="passengerTypes[${i.index}].passengerType.name" type="hidden" readonly="true" value="${fn:escapeXml(entry.passengerTypeQuantity.passengerType.name)}" />
		<form:input path="passengerTypes[${i.index}].quantity" type="hidden" readonly="true" value="${fn:escapeXml(entry.passengerTypeQuantity.quantity)}" />
	</c:forEach>
	<deallisting:dealavailabledates departureDate="${departureDate}" returnDate="${returnDate}" dealStartDatePattern="${packageResponseData.startingDatePattern}" />
	<div class="col-xs-12">
	    <input type="hidden" class="isPackageResponseDataAvailable" value="${packageResponseData.available}" />
		<button type="submit" class="btn btn-secondary col-xs-12 y_addDealToCartSubmit" disabled >
			<spring:message code="button.page.deallisting.deal.available.dates.select" text="Select" />
		</button>
	</div>
</form:form>

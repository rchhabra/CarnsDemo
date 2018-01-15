<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="deallisting" tagdir="/WEB-INF/tags/addons/travelcommons/responsive/deallisting"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="dealBundleTemplateId" required="true" type="java.lang.String"%>
<%@ attribute name="displayForm" required="true" type="java.lang.Boolean"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:set var="itineraries" value="${packageResponseData.transportPackageResponse.fareSearchResponse.pricedItineraries}" />
<c:set var="accommodationAvailabilityResponse" value="${packageResponseData.accommodationPackageResponse.accommodationAvailabilityResponse}" />
<fmt:formatDate value="${accommodationAvailabilityResponse.roomStays[0].checkInDate}" pattern="dd/MM/yyyy" var="departureDate" />
<fmt:formatDate value="${accommodationAvailabilityResponse.roomStays[0].checkOutDate}" pattern="dd/MM/yyyy" var="returnDate" />
<c:url var="submitUrl" value="/cart/addDeal" />
<div class="panel panel-accommodation panel-date-change">
	<fieldset>
		<form:form action="${submitUrl}" modelAttribute="addDealToCartForm" method="POST" class="y_addDealToCartForm">
			<div class="alert alert-danger" style="${packageResponseData.available?'display: none;' : ''}">
				<p id="y_dealDepartureError">
					<spring:message code="text.page.dealdetails.deal.sold.out" text="Deal is no longer available for selected dates " />
				</p>
			</div>
			<legend class="sr-only">
				<spring:message code="text.page.dealdetails.deal.changedates.sr" text="Change dates" />
			</legend>
			<div class="col-xs-12 col-sm-3">
				<p>
					<spring:message code="text.page.dealdetails.deal.changedates.title" text="Would you like to change the dates of your holiday?" />
				</p>
			</div>
			<div class="form-group col-xs-12 col-sm-3">
				<label class="sr-only" for="dealDatePickerDeparting">
					<spring:message code="text.page.dealdetails.deal.datepicker.departure.message" text="Departing" />
				</label>
				<form:input type="text" path="startingDate" class="datePickerDeparting col-xs-12 datepicker input-grid form-control" data-dealcomponentid="${fn:escapeXml(dealComponentId)}" data-dealbundletemplateid="${fn:escapeXml(dealBundleTemplateId)}" data-dealstartdatepattern="${fn:escapeXml(packageResponseData.startingDatePattern)}"
					id="dealDatePickerDeparting" value="${fn:escapeXml(departureDate)}" placeholder="Departing" autocomplete="off" />
			</div>
			<div class="form-group col-xs-12 col-sm-3 nowrap">
				<label class="sr-only" for="dealReturning">
					<spring:message code="text.page.dealdetails.deal.datepicker.return.message" text="Returning" />
				</label>
				<form:input type="text" path="endingDate" class="col-xs-12 input-grid form-control" id="dealDatePickerReturning" name="dealReturning_${fn:escapeXml(dealBundleTemplateId)}" value="${fn:escapeXml(returnDate)}" placeholder="Returning" autocomplete="off" readonly="true" />
			</div>
			<c:if test="${displayForm}">
				<form:input type="hidden" path="bundleTemplateID" value="${dealBundleTemplateId}" />
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
			</c:if>
			<div class="col-xs-12 col-sm-3">
				<button type="submit" class="btn btn-default btn-block y_dealUpdateDate" ${packageResponseData.available && displayForm ? ' ' : ' disabled' }>
					<spring:message code="button.page.dealdetails.deal.available.dates.update" text="Update" />
				</button>
			</div>
		</form:form>
	</fieldset>
</div>

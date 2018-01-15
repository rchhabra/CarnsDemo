<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ attribute name="formPrefix" required="true" type="java.lang.String"%>
<%@ attribute name="tripType" required="true" type="java.lang.String"%>
<%@ attribute name="idPrefix" required="true" type="java.lang.String"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<%-- One way / return radio --%>
<div class="radio-button-row">
	<form:radiobutton id="y_roundTripRadbtn" path="${fn:escapeXml(formPrefix)}tripType" value="RETURN" class="y_fareFinderTripTypeBtn" />
	<label for="roundTripRadbtn">
		<spring:theme code="text.cms.farefinder.triptype.roundtrip" text="Round Trip" />
	</label>
	<form:radiobutton id="y_oneWayRadbtn" path="${fn:escapeXml(formPrefix)}tripType" value="SINGLE" class="y_fareFinderTripTypeBtn" />
	<label for="oneWayRadbtn">
		<spring:theme code="text.cms.farefinder.triptype.oneway" text="One Way" />
	</label>
</div>
<%-- / One way / return radio --%>
<%-- From / To input fields --%>
<div class="row input-row">
	<div class="input-required-wrap col-xs-12 col-sm-6">
		<label for="y_originLocation">
			<spring:theme var="departureLocationPlaceholderText" code="text.cms.farefinder.departure.location.placeholder" text="From" />
		</label>
		<form:input type="text" id="y_originLocation" path="${fn:escapeXml(formPrefix)}departureLocationName" cssErrorClass="fieldError" class="y_originLocation input-grid col-xs-12 form-control" placeholder="${fn:escapeXml(departureLocationPlaceholderText)}" autocomplete="off" />
		<form:hidden path="${fn:escapeXml(formPrefix)}departureLocation" class="y_originLocationCode" />
		<form:hidden path="${fn:escapeXml(formPrefix)}departureLocationSuggestionType" class="y_originLocationSuggestionType" />
		<div id="y_originLocationSuggestions" class="autocomplete-suggestions-wrapper hidden"></div>
	</div>
	<div class="input-required-wrap col-xs-12 col-sm-6">
		<label for="y_destinationLocation">
			<spring:theme var="arrivalLocationPlaceholderText" code="text.cms.farefinder.arrival.location.placeholder" text="To" />
		</label>
		<form:input type="text" id="y_destinationLocation" path="${fn:escapeXml(formPrefix)}arrivalLocationName" cssErrorClass="fieldError" class="col-xs-12 y_destinationLocation input-grid form-control" placeholder="${fn:escapeXml(arrivalLocationPlaceholderText)}" autocomplete="off" />
		<form:hidden path="${fn:escapeXml(formPrefix)}arrivalLocation" class="y_destinationLocationCode" />
		<form:hidden path="${fn:escapeXml(formPrefix)}arrivalLocationSuggestionType" class="y_destinationLocationSuggestionType" />
		<div id="y_destinationLocationSuggestions" class="autocomplete-suggestions-wrapper hidden"></div>
	</div>
</div>
<%-- / From / To input fields --%>
<%-- Departing / Returning dates input fields --%>
<div class="row input-row">
	<div class="input-required-wrap col-xs-12 col-sm-6">
		<label for="departingDateTime">
			<spring:theme var="departingDatePlaceholderText" code="text.cms.farefinder.departure.date.placeholder" text="Departure Date" />
		</label>
		<form:input type="text" path="${fn:escapeXml(formPrefix)}departingDateTime" class="col-xs-12 datepicker input-grid form-control y_${fn:escapeXml( idPrefix )}FinderDatePickerDeparting y_transportDepartDate" placeholder="${fn:escapeXml(departingDatePlaceholderText)}" autocomplete="off" />
	</div>
	<c:choose>
		<c:when test="${(tripType == 'RETURN') || (tripType == null)}">
			<c:set var="showReturnOptions" scope="session" value="true" />
		</c:when>
		<c:otherwise>
			<c:set var="showReturnOptions" scope="session" value="false" />
		</c:otherwise>
	</c:choose>
	<div class="y_fareFinderReturnField" style="display: ${showReturnOptions ? 'block' : 'none'};">
		<div class="input-required-wrap col-xs-12 col-sm-6">
			<label for="returnDateTime">
				<spring:theme var="returnDatePlaceholderText" code="text.cms.farefinder.return.date.placeholder" text="Return Date" />
			</label>
			<form:input type="text" path="${fn:escapeXml(formPrefix)}returnDateTime" class="returnOption col-xs-12 datepicker input-grid form-control y_${fn:escapeXml( idPrefix )}FinderDatePickerReturning y_transportReturnDate" placeholder="${fn:escapeXml(returnDatePlaceholderText)}" autocomplete="off" />
		</div>
	</div>
</div>
<%-- Departing / Returning dates input fields --%>
<div class="row input-row less-margin">
	<div class="col-xs-12 col-sm-6">
		<label class="sr-only" for="flightClass">
			<spring:theme var="defaultCabinClassText" code="text.cms.farefinder.default.cabin.class" text="Select a Class" />
		</label>
		<form:select class="form-control" id="flightClass" path="${fn:escapeXml(formPrefix)}cabinClass" multiple="no">
			<form:option value="default" disabled="true">${fn:escapeXml(defaultCabinClassText)}</form:option>
			<form:options items="${cabinClasses}" itemValue="code" itemLabel="name" htmlEscape="true"/>
		</form:select>
		<c:set var="specialAssistanceText">
			<spring:theme code="text.cms.farefinder.special.assistance"
						  text="Please contact us at least 48 hours before your flight departs in order to help us make your journey as easy as possible even if your flight is operated by one of our airline alliance or franchise partners because they may have different restrictions. If you have any questions please call us. To ensure we can provide the best possible service." />
		</c:set>
		<a tabindex="0" class="link-trigger" role="button" data-toggle="popover" data-trigger="focus" data-placement="bottom" data-container="body" data-content="${fn:escapeXml(specialAssistanceText)}">
			<spring:theme code="text.cms.farefinder.specialrequirements" text="Special Assistance" />
			&nbsp;<i class="glyphicon glyphicon-info-sign"></i>
		</a>
	</div>
</div>

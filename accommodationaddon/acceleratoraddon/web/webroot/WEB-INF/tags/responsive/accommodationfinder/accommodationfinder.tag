<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ attribute name="formPrefix" required="true" type="java.lang.String"%>
<%@ attribute name="idPrefix" required="true" type="java.lang.String"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<div class="row input-row" style="display: ${showAccommodationDestination ? 'block' : 'none'};">
	<div class="input-required-wrap col-xs-12">
		<label class="sr-only" for="y_accommodationFinderLocation">
			<spring:theme var="destinationLocationPlaceholderText" code="text.cms.accommodationfinder.destination.placeholder" text="Where are you going?" />
			${fn:escapeXml(destinationLocationPlaceholderText)}
		</label>
		<form:input type="search" class="y_accommodationFinderLocation input-grid col-xs-12 form-control" id="y_accommodationFinderLocation" path="${fn:escapeXml(formPrefix)}destinationLocationName" cssErrorClass="fieldError" placeholder="${fn:escapeXml(destinationLocationPlaceholderText)}" autocomplete="off" />
		<form:hidden path="${fn:escapeXml(formPrefix)}destinationLocation" class="y_accommodationFinderLocationCode" />
		<form:hidden path="${fn:escapeXml(formPrefix)}suggestionType" class="y_accommodationFinderLocationSuggestionType" />
		<form:hidden path="${fn:escapeXml(formPrefix)}latitude" class="y_accommodationFinderLocationLatitude" />
		<form:hidden path="${fn:escapeXml(formPrefix)}longitude" class="y_accommodationFinderLocationLongitude" />
		<form:hidden path="${fn:escapeXml(formPrefix)}radius" class="y_accommodationFinderLocationRadius" />
		<div id="y_accommodationFinderLocationSuggestions" class="hidden clearfix"></div>
	</div>
</div>
<div class="row input-row checkInAndCheckOutDiv" style="display: ${showCheckInCheckOut ? 'block' : 'none'};">
	<div class="col-xs-9 col-sm-8">
		<div class="row">
			<div class="input-required-wrap col-xs-12 col-sm-6">
				<input id="y_maxAllowedCheckInCheckOutDateDifference" type="hidden" value="${fn:escapeXml(maxAllowedCheckInCheckOutDateDifference)}" />
				<label class="sr-only" for="y_${fn:escapeXml( idPrefix)}DatePickerCheckIn">
					<spring:theme var="checkInDatePlaceholderText" code="text.cms.accommodationfinder.checkin.date.placeholder" text="Check In" />
				</label>
				<form:input type="text" id="y_${fn:escapeXml( idPrefix)}DatePickerCheckIn" path="${fn:escapeXml(formPrefix)}checkInDateTime" class="datePickerDeparting col-xs-12 datepicker input-grid form-control y_${fn:escapeXml( idPrefix)}FinderDatePickerCheckIn" placeholder="Check In" autocomplete="off" />
			</div>
			<div class="input-required-wrap col-xs-12 col-sm-6">
				<label class="sr-only" for="y_${fn:escapeXml( idPrefix)}DatePickerCheckOut">
					<spring:theme var="checkOutDatePlaceholderText" code="text.cms.accommodationfinder.checkout.date.placeholder" text="Check Out" />
				</label>
				<form:input type="text" id="y_${fn:escapeXml( idPrefix)}DatePickerCheckOut" path="${fn:escapeXml(formPrefix)}checkOutDateTime" class="datePickerReturning col-xs-12 datepicker input-grid form-control y_${fn:escapeXml( idPrefix)}FinderDatePickerCheckOut" placeholder="Check Out" autocomplete="off" />
			</div>
		</div>
	</div>
	<div class="col-xs-3 col-sm-4"> 
		<div class="row">
			<div class="col-xs-offset-1 col-xs-10 col-sm-offset-0 col-sm-8">
				<div class="night-number">
					<p>
						<span id="y_numberOfNights">0</span>
						<span class="nights-placeholder-text">
							<spring:theme var="noOfNightsPlaceholderText" code="text.cms.accommodationfinder.numberofnights.placeholder" text="nights" />
							${fn:escapeXml(noOfNightsPlaceholderText)}
						</span>
						<span class="nights-placeholder-text hidden">
							<spring:theme var="noOfNightsPlaceholderText" code="text.cms.accommodationfinder.numberofnight.placeholder" text="night" />
							${fn:escapeXml(noOfNightsPlaceholderText)}
						</span>
					</p>
					<span class="moon"></span>
				</div>
			</div>
		</div>
	</div>
</div>

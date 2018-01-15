<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ attribute name="departureDate" required="true" type="java.lang.String"%>
<%@ attribute name="returnDate" required="true" type="java.lang.String"%>
<%@ attribute name="dealStartDatePattern" required="true" type="java.lang.String"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="col-xs-12">
	<div class="row">
		<fieldset>
			<div class="alert alert-danger" style="display: none;">
				<p id="y_dealDepartureError_${fn:escapeXml(dealComponentId)}"/>
			</div>
			<legend class="sr-only">
				<spring:message code="text.page.deallisting.deal.available.dates.sr" text="Available dates" />
			</legend>
			<div class="col-xs-12">
				<label class="sr-only" for="dealDatePickerDeparting_${fn:escapeXml(dealComponentId)}">
					<spring:message code="text.page.deallisting.deal.datepicker.departure.message" text="Departing" />
				</label>
				<form:input type="text" path="startingDate" class="datePickerDeparting col-xs-12 datepicker input-grid form-control" data-dealcomponentid="${fn:escapeXml(dealComponentId)}" data-dealbundletemplateid="${fn:escapeXml(dealBundleTemplateId)}" data-dealstartdatepattern="${fn:escapeXml(dealStartDatePattern)}"
					id="dealDatePickerDeparting_${fn:escapeXml(dealComponentId)}" value="${fn:escapeXml(departureDate)}" placeholder="Departing" autocomplete="off" />
			</div>
			<div class="col-xs-12">
				<label class="sr-only" for="dealReturning_01">
					<spring:message code="text.page.deallisting.deal.datepicker.return.message" text="Returning" />
				</label>
				<form:input type="text" path="endingDate" class="col-xs-12 input-grid form-control" id="dealReturning_${fn:escapeXml(dealComponentId)}" name="dealReturning_${fn:escapeXml(dealComponentId)}" value="${fn:escapeXml(returnDate)}" placeholder="Returning" autocomplete="off" readonly="true" />
			</div>
		</fieldset>
	</div>
</div>

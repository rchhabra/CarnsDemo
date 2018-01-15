<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="formId" required="true" type="java.lang.String"%>
<%@ attribute name="action" required="true" type="java.lang.String"%>
<%@ attribute name="method" required="true" type="java.lang.String"%>
<%@ attribute name="orientation" required="false" type="java.lang.String"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:choose>
	<c:when test="${orientation eq null || orientation eq 'v' }">
		<form:form id="${fn:escapeXml(formId)}" method="${fn:escapeXml(method)}" autocomplete="off" commandName="transportOfferingStatusForm" action="${fn:escapeXml(action)}" class="col-xs-12 form-background">
			<fieldset>
				<legend class="primary-legend">
					<a class="panel-heading panel-header-link collapsable collapsed y_flightStatusPanelHeaderLink" role="button" data-toggle="collapse" href="#flight-status" aria-expanded="false" aria-controls="manage-booking"><spring:theme code="status.search.title" text="Flight Status" /></a>
				</legend>
				<%-- Flight Number / Departure Date input fields --%>
				<div class="panel-body collapse" id="flight-status">
					<div class="row input-row stacked">
						<label class="sr-only" for="transportOfferingNumber">
							<spring:theme code="sr.status.search.flightnumber" text="Flight Number" />
						</label>
						<spring:theme code="status.search.flight.number.placeholder" var="transportOfferingNumber" />
						<form:input path="transportOfferingNumber" type="search" class="col-xs-12 input-grid form-control" placeholder="${fn:escapeXml(transportOfferingNumber)}" />
						<form:errors path="transportOfferingNumber" cssClass="fe-error" />
						<label class="sr-only" for="departureDate">
							<spring:theme code="sr.status.search.departuredate" text="Departure Date" />
						</label>
						<spring:theme code="status.search.departure.date.placeholder" var="departureDatePlaceholder" />
						<form:input path="departureDate" type="search" class="y_transportOfferingStatusDepartureDate col-xs-12 datepicker input-grid form-control" placeholder="${fn:escapeXml(departureDatePlaceholder)}" />
						<form:errors path="departureDate" cssClass="fe-error" />
						<div class="col-xs-12 btn-wrapper">
							<form:button class="btn btn-primary col-xs-12 y_flightStatusSearchBtn" disabled="true">
								<spring:theme code="status.search.button" text="Search"/>
							</form:button>
						</div>
					</div>
				</div>				
			</fieldset>
		</form:form>
	</c:when>
	<c:otherwise>
		<div class="panel panel-primary">
			<div class="panel-heading">
				<h2 class="panel-title">
					<spring:theme code="status.search.title" text="Flight Status" />
				</h2>
			</div>
			<div class="panel-body">
				<form:form id="${fn:escapeXml(formId)}" method="${fn:escapeXml(method)}" autocomplete="off" commandName="transportOfferingStatusForm" action="${fn:escapeXml(action)}">
					<fieldset>
						<div class="form-group col-xs-12 col-sm-12 col-md-6">
							<label for="transportOfferingNumber">
								<spring:theme code="sr.status.search.flightnumber" text="Flight Number" />
							</label>
							<form:input path="transportOfferingNumber" type="search" class="col-xs-12 form-control" placeholder="${fn:escapeXml(transportOfferingNumber)}" />
							<form:errors path="transportOfferingNumber" cssClass="fe-error" />
						</div>
						<div class="form-group col-xs-12 col-sm-12 col-md-6">
							<label for="departureDate">
								<spring:theme code="sr.status.search.departuredate" text="Departure Date" />
							</label>
							<form:input path="departureDate" type="search" class="y_transportOfferingStatusDepartureDate col-xs-12 datepicker form-control" placeholder="${fn:escapeXml(departureDateFormat)}" />
							<form:errors path="departureDate" cssClass="fe-error" />
						</div>
						<div class="form-group col-xs-12 col-sm-12 col-md-offset-6 col-md-6 bottom-align-parent">
							<form:button class="btn btn-primary btn-block bottom-align">
								<spring:theme code="status.search.button" />
							</form:button>
						</div>
					</fieldset>
				</form:form>
			</div>
		</div>
	</c:otherwise>
</c:choose>

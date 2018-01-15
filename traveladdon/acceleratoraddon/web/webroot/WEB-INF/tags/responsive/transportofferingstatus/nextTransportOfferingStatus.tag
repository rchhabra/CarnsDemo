<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="formId" required="true" type="java.lang.String"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<form:form id="${fn:escapeXml(formId)}" class="col-xs-12 form-background">
	<input type="hidden" value="${fn:escapeXml(component.uid)}" id="y_transportOfferingStatusSearchComponentId" />
	<fieldset>
		<legend class="primary-legend">
			<a class="panel-heading panel-header-link collapsable collapsed" role="button" data-toggle="collapse" href="#flight-status" aria-expanded="false" aria-controls="manage-booking"><spring:theme code="next.transport.offering.status.title" text="Next Flight Status" /></a>
		</legend>
		
		<div id="flight-status" class="panel-body collapse">
			<div class="row input-row stacked">
				<p for="transportOfferingStatus" class="row input-row stacked">
					<spring:theme code="next.transport.offering.status.text" text="Your next flight {0} from {1} to {2} is " arguments="${fn:escapeXml(nextScheduledTransportOfferingData.travelProvider.code)}${fn:escapeXml(nextScheduledTransportOfferingData.number)},${fn:escapeXml(nextScheduledTransportOfferingData.sector.origin.name)},${fn:escapeXml(nextScheduledTransportOfferingData.sector.destination.name)}" />
					<span>${fn:escapeXml(nextScheduledTransportOfferingData.status)}</span>
					at
					<fmt:formatDate value="${nextScheduledTransportOfferingData.departureTime}" var="formattedDepartureDate" type="both" pattern="HH:mm dd MMM yyyy" />
					<dd>${fn:escapeXml(formattedDepartureDate)}</dd>
				</p>
				<div class="col-xs-12 y_transportOfferingStatusSearchTrigger">
					<button class="btn btn-primary col-xs-12 y_checkOtherFlights" disabled>
						<spring:theme code="next.transport.offering.status.other" text="Check other flights" />
					</button>	
				</div>
			</div>
		</div>
	</fieldset>
</form:form>

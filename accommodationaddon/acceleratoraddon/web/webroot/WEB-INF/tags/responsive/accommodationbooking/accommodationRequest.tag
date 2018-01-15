<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ attribute name="actionData" required="true" type="de.hybris.platform.commercefacades.travel.AccommodationBookingActionData"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:set var="roomStayRefNumber" value="${actionData.roomStayRefNumber}" />
<c:url var="submitRequestUrl" value="${actionData.actionUrl}" />
<form:form id="y_addRequestForm_rs${fn:escapeXml(roomStayRefNumber)}" modelAttribute="addRequestForm" action="${submitRequestUrl}" method="POST" class="y_addRequestForm">
	<fieldset class="fieldset">
		<legend class="sr-only">
			<spring:theme code="accommodation.booking.details.page.request" />
		</legend>
	</fieldset>

	<div class="form-group col-xs-12">
			<label for="rm${fn:escapeXml(roomStayRefNumber)}_request-message">
				<spring:theme code="accommodation.booking.details.page.request.label"/>
			</label>
			<form:textarea path="requestMessage" class="y_requestMessage col-xs-12 form-control" id="rm${roomStayRefNumber}_request-message" maxlength="255" />
			<form:input type="hidden" path="roomStayRefNumber" value="${roomStayRefNumber }" />
	</div>
	<div class="form-group col-xs-12 col-sm-4 col-xs-offset-0 col-sm-offset-8">
		<button type="submit" class="btn btn-primary btn-block y_addRequestButton">
			<spring:theme code="accommodation.booking.details.page.request" />
		</button>
	</div>
</form:form>

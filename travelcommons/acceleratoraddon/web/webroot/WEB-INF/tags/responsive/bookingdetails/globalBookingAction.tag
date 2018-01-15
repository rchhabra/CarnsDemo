<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ attribute name="bookingActionResponseData" required="true" type="de.hybris.platform.commercefacades.travel.BookingActionResponseData"%>
<%@ attribute name="actionType" required="true" type="java.lang.String"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<c:forEach items="${bookingActionResponseData.globalBookingActions}" var="bookingActionData">
	<c:if test="${actionType == bookingActionData.actionType}">
		<c:set var="actionData" value="${bookingActionData}" />
	</c:if>
</c:forEach>

<c:if test="${actionData.enabled}">

	<c:if test="${actionType == 'CANCEL_BOOKING'}">
		<c:set var="y_class" value="y_cancelBookingButton" />
		<c:set var="disabled" value="true" />
	</c:if>

	<c:url value="${actionData.actionUrl}" var="actionUrl" />
	<a class="btn btn-primary btn-block ${y_class}" href="${actionUrl}" ${disabled ? 'disabled' : ''} >
		<spring:theme code="button.booking.details.travel.global.booking.action.${actionType}" text="${fn:escapeXml(fn:toLowerCase(actionType))}" />
	</a>

</c:if>

<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ attribute name="bookingActionResponseData" required="true" type="de.hybris.platform.commercefacades.travel.BookingActionResponseData" %>
<%@ attribute name="actionType" required="true" type="java.lang.String" %>
<%@ attribute name="travellerUid" type="java.lang.String" %>
<%@ attribute name="leg" type="java.lang.String" %>
<%@ attribute name="actionName" type="java.lang.String" %>
<spring:htmlEscape defaultHtmlEscape="true" />


<c:forEach items="${bookingActionResponseData.bookingActions}" var="bookingActionData">
	<c:if test="${actionType == bookingActionData.actionType}">
		<c:choose>
			<c:when test="${actionType == 'AMEND_ANCILLARY' || actionType == 'CANCEL_BOOKING' || actionType == 'CANCEL_TRANSPORT_BOOKING' || actionType == 'ACCEPT_BOOKING' || actionType == 'REJECT_BOOKING' }">
				<c:set var="actionData" value="${bookingActionData}" />
			</c:when>
			<c:otherwise>
				<fmt:parseNumber var="originDestinationRefNumber" type="number" value="${leg}" />
				<c:if test="${originDestinationRefNumber == bookingActionData.originDestinationRefNumber}">
					<c:choose>
						<c:when test="${actionType == 'CHECK_IN_ALL'}">
							<c:set var="actionData" value="${bookingActionData}" />
						</c:when>					
						<c:otherwise>
							<c:if test="${travellerUid == bookingActionData.traveller.uid}">
								<c:set var="actionData" value="${bookingActionData}" />
							</c:if>
						</c:otherwise>
					</c:choose>
				</c:if>
			</c:otherwise>
		</c:choose>
	</c:if>
</c:forEach>

<c:choose>
	<c:when test="${actionData.enabled}">
	
		<c:if test="${actionType == 'CANCEL_BOOKING'}">
			<c:set var="y_class" value="y_cancelBookingButton" />
			<c:set var="disabled" value="true" />
		</c:if>
		<c:if test="${actionType == 'CANCEL_TRANSPORT_BOOKING'}">
			<c:set var="y_class" value="y_cancelTransportBookingButton" />
			<c:set var="disabled" value="true" />
		</c:if>
		<c:if test="${actionType == 'REMOVE_TRAVELLER'}">
			<c:set var="y_class" value="y_removeTraveller" />
		</c:if>

		<c:if test="${actionType == 'ACCEPT_BOOKING'}">
			<c:set var="y_class" value="y_acceptBookingButton" />
		</c:if>
		<c:if test="${actionType == 'REJECT_BOOKING'}">
			<c:set var="y_class" value="y_cancelBookingButton" />
		</c:if>
		
		<c:url value="${actionData.actionUrl}" var="actionUrl" />
		<a class="btn ${actionType == 'CHECK_IN_ALL'? 'btn-secondary' : 'btn-primary'} btn-block ${fn:escapeXml(y_class)}" href="${actionUrl}" ${disabled ? 'disabled' : ''}
		<c:if test="${actionType == 'REMOVE_TRAVELLER'}">
			style="pointer-events: none;"
		</c:if>
		>
			<c:choose>
				<c:when test="${not empty actionName}">
					<c:out value="${actionName}" />
				</c:when>
				<c:otherwise>
					<spring:theme code="button.page.managemybooking.bookingaction.${actionType}" text="${fn:escapeXml(fn:toLowerCase(actionType))}" />
				</c:otherwise>
			</c:choose>
		</a>

	</c:when>
	<c:otherwise>
		<c:if test="${not empty actionData.alternativeMessages && actionType == 'CHECK_IN'}">
			<c:forEach items="${actionData.alternativeMessages}" var="message">
				<c:set var="messageText">
					<spring:theme code="${message}" />
				</c:set>
				<c:if test="${not empty messageText}">
					<button class="btn btn-default btn-block" disabled="disabled">${fn:escapeXml(messageText)}</button>
				</c:if>
			</c:forEach>
		</c:if>	
	</c:otherwise>	

</c:choose>




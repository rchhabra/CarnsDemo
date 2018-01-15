<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="roomStayReferenceNumber" required="true" type="java.lang.Integer"%>
<%@ attribute name="service" required="true" type="de.hybris.platform.commercefacades.accommodation.ServiceData"%>
<%@ attribute name="index" required="true" type="java.lang.Integer"%>
<%@ attribute name="roomStayIndex" required="true" type="java.lang.Integer"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<c:forEach var="reservedService" items="${reservationData.roomStays[roomStayIndex].services}">
	<c:if test="${reservedService.serviceDetails.product.code == service.code}">
		<c:set var="reservedQuantity" value="${reservedService.quantity}" />
	</c:if>
</c:forEach>

<c:set var="label" value="rm_${roomStayIndex}-extra-item-${index}" />
<label for="${fn:escapeXml(label)}" class="sr-only">${fn:escapeXml(service.serviceDetails.product.name)}</label>
<select class="form-control y_extraServiceSelect" id="${fn:escapeXml(label)}">
	<option ${reservedQuantity == null ? 'selected' : '' } disabled>
		<spring:theme code="text.guest.details.extra.services.select.quantity" text="Select Quantity" />
	</option>
	<c:forEach var="quantity" begin="${service.serviceDetails.restriction.minQuantity}" end="${service.serviceDetails.restriction.maxQuantity}">
		<option value="${fn:escapeXml(quantity)}" ${reservedQuantity == quantity ? 'selected' : '' }>${fn:escapeXml(quantity)}</option>
	</c:forEach>
</select>
<input type="hidden" id="${fn:escapeXml(label)}-productCode" value="${fn:escapeXml(service.serviceDetails.product.code)}"/>
<input type="hidden" id="${fn:escapeXml(label)}-roomStayReferenceNumber" value="${fn:escapeXml(roomStayReferenceNumber)}" class="y_roomStayRefNumber_${fn:escapeXml(roomStayReferenceNumber)}"/>

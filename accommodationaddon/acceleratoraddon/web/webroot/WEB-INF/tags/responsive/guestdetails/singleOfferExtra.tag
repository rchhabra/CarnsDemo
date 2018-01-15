<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="roomStayReferenceNumber" required="true" type="java.lang.Integer"%>
<%@ attribute name="service" required="true" type="de.hybris.platform.commercefacades.accommodation.ServiceData"%>
<%@ attribute name="index" required="true" type="java.lang.Integer" %>
<%@ attribute name="roomStayIndex" required="true" type="java.lang.Integer" %>

<c:forEach var="reservedService" items="${reservationData.roomStays[roomStayIndex].services}">
	<c:if test="${reservedService.serviceDetails.product.code == service.code}">
		<c:set var="reserved" value="true" />
	</c:if>
</c:forEach>

<c:set var="label" value="rm_${roomStayIndex}-extra-item-${index}" />
<label for="${label}" class="sr-only">${fn:escapeXml(service.serviceDetails.product.name)}</label>
<input type="checkbox" class="y_extraServiceCheckbox" value="${fn:escapeXml(service.serviceDetails.restriction.maxQuantity)}" id="${fn:escapeXml(label)}" ${reserved ? 'checked' : '' }>
<input type="hidden" id="${label}-productCode" value="${fn:escapeXml(service.serviceDetails.product.code)}"/>
<input type="hidden" id="${label}-roomStayReferenceNumber" value="${fn:escapeXml(roomStayReferenceNumber)}" class="y_roomStayRefNumber_${fn:escapeXml(roomStayReferenceNumber)}"/>

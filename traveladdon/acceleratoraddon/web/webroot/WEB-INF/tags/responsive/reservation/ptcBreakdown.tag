<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="ptcBreakdownList" required="true" type="java.util.List"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<div class="sr-only"><spring:theme code="sr.itinerary.passengers" /></div>
<c:forEach items="${ptcBreakdownList}" var="ptcFareBreakdown" varStatus="ptcIdx">
	<p>${fn:escapeXml(ptcFareBreakdown.passengerTypeQuantity.quantity)}&nbsp;x&nbsp;${fn:escapeXml(ptcFareBreakdown.passengerTypeQuantity.passengerType.name)}:&nbsp;<strong>${fn:escapeXml(ptcFareBreakdown.passengerFare.baseFare.formattedValue)}</strong></p>
</c:forEach>

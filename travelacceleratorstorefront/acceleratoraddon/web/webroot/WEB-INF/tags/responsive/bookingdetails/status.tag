<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="code" required="true" type="java.lang.String" %>
<%@ attribute name="name" required="true" type="java.lang.String" %>
<spring:htmlEscape defaultHtmlEscape="true" />

<c:set var="bookingStatus">
    <spring:theme code="booking.status.${code}" text="" />
</c:set>

<c:choose>
    <c:when test="${not empty bookingStatus && bookingStatus != ''}">
        ${fn:escapeXml(bookingStatus)}
    </c:when>
    <c:otherwise>
        ${fn:escapeXml(name)}
    </c:otherwise>
</c:choose>

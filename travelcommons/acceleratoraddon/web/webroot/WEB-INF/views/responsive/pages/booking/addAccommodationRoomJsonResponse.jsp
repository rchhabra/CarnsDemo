<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="bookingdetails" tagdir="/WEB-INF/tags/addons/travelcommons/responsive/bookingdetails"%>
<json:object escapeXml="false">
	<json:property name="valid" value="${!hasErrorFlag}" />
	<json:property name="addAccommodationRoomHtmlContent">
		<c:choose>
			<c:when test="${hasErrorFlag}">
				<spring:message code="${errorMsg}" />
			</c:when>
			<c:otherwise>
				<bookingdetails:addAccommodationRoom />
			</c:otherwise>
		</c:choose>
	</json:property>
</json:object>
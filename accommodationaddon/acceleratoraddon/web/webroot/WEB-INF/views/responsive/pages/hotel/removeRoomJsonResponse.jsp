<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<json:object escapeXml="false">
	<c:forEach var="item" items="${removeRoomResult}">
		<json:property name="${item.key}">
			 <spring:theme code="${item.value}" />
		</json:property>
	</c:forEach>
</json:object>

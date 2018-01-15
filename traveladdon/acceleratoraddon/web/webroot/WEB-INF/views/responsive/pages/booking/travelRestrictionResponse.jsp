<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<json:object escapeXml="false">

	<json:property name="hasErrorFlag" value="${not empty restrictionErrors}"/>
	
  	<json:array name="errors">
		<c:forEach var="error" items="${restrictionErrors}">
			<json:property><spring:theme code="text.ancillary.travel.restriction.modal.error" arguments="${error.key},${error.value}" /></json:property>
		</c:forEach>
  	</json:array>
	
</json:object>


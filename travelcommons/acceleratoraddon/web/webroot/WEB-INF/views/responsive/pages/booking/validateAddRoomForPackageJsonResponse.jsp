<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<json:object escapeXml="false">
	<json:property name="valid" value="${!hasErrorFlag}" />
	<c:if test="${hasErrorFlag}">
		<json:property name="errorMsg">
			<spring:message code="${errorMsg}" />
		</json:property>
	</c:if>
</json:object>
<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<c:if test="${not empty dealValidDates}">
	<json:array items="${dealValidDates}" var="dealValidDate">
		<json:object escapeXml="false">
			<json:property name="dealValidDate" value="${dealValidDate}" />
		</json:object>
	</json:array>
</c:if>

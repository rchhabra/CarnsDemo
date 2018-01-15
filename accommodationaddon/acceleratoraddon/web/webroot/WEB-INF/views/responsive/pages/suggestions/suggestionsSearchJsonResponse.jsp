<%@ page trimDirectiveWhitespaces="true" contentType="application/json" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>
<%@ taglib prefix="suggestions" tagdir="/WEB-INF/tags/addons/accommodationaddon/responsive/suggestions"%>

<json:object escapeXml="false">
	<json:property name="htmlContent">
		<c:if test="${not empty suggestionResult}">
			<suggestions:suggestionResults/>
		</c:if>
	</json:property>
</json:object>


<%@ page trimDirectiveWhitespaces="true" contentType="application/json" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>
<%@ taglib prefix="suggestions" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/suggestions"%>

<json:object escapeXml="false">
	<json:property name="htmlContent">
		<suggestions:suggestionResults/>
	</json:property>
</json:object>


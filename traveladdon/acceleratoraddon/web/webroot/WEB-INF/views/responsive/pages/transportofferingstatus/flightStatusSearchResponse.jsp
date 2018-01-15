<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>
<%@ taglib prefix="transportofferingstatus" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/transportofferingstatus"%>

<json:object escapeXml="false">

	<json:property name="hasErrorFlag" value="${hasErrorFlag}" />
	
	<json:property name="htmlContent">
		<transportofferingstatus:statusResults ></transportofferingstatus:statusResults>
	</json:property>
</json:object>


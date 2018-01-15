<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>
<%@ taglib prefix="fareselection" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/fareselection"%>

<json:object escapeXml="false">
	<json:property name="refNumber" value="${refNumber}" />
	<json:property name="htmlContent">
		<fareselection:offeringList fareSelection="${fareSelection}" refNumber="${refNumber}" />
	</json:property>
</json:object>


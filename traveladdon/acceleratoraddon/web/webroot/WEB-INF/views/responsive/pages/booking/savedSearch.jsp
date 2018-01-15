<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<json:object escapeXml="false">
	<json:property name="message">
		<spring:theme code="${savedSearchMessage}" />
	</json:property>
</json:object>

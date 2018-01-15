<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<c:if test="${not empty dealChangeDateValidationError}">
	<json:object escapeXml="false">
		<json:property name="dealChangeDateValidationError">
			<spring:message code="error.page.deallisting.deal.changedate.${dealChangeDateValidationError}" />
		</json:property>
	</json:object>
</c:if>

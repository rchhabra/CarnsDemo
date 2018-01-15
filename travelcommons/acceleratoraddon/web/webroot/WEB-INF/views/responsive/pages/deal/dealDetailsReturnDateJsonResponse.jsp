<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="dealdetails" tagdir="/WEB-INF/tags/addons/travelcommons/responsive/dealdetails"%>
<c:choose>
	<c:when test="${not empty dealChangeDateValidationError}">
		<json:object escapeXml="false">
			<json:property name="dealChangeDateValidationError">
				<spring:message code="error.page.deallisting.deal.changedate.${dealChangeDateValidationError}" />
			</json:property>
		</json:object>
	</c:when>
	<c:otherwise>
		<json:object escapeXml="false">
			<json:property name="dealChangeDateHtmlContent">
				<dealdetails:dealUpdateDates dealBundleTemplateId="${dealBundleTemplateId}" displayForm="true" />
			</json:property>
		</json:object>
	</c:otherwise>
</c:choose>
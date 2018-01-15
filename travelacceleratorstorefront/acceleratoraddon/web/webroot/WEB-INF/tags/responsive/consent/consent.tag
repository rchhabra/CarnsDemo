<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ attribute name="consentFormPath" type="java.lang.String" required="true"%>
<%@ attribute name="consentTemplateData" type="de.hybris.platform.commercefacades.consent.data.ConsentTemplateData" required="true"%>
<c:if test="${ not empty consentTemplateData }">
	<form:hidden path="${consentFormPath}.consentTemplateId" value="${consentTemplateData.id}" />
	<form:hidden path="${consentFormPath}.consentTemplateVersion" value="${consentTemplateData.version}" />
	<div class="checkbox">
		<label class="control-label uncased">
			<form:checkbox path="${consentFormPath}.consentGiven" />
			<c:out value="${consentTemplateData.description}" />
			&nbsp;
			<spring:theme code="registration.consent.link" />
		</label>
	</div>
</c:if>
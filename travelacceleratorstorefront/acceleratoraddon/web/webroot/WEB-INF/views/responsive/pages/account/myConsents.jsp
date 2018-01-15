<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="panel-heading">
	<h3 class="title title-collapse">
		<spring:theme code="text.account.consent.title" />
	</h3>
	<button class="show-hide-button" data-toggle="collapse" data-target="#consent-management-form" aria-expanded="true" aria-controls="consent-management-form">
		<spring:theme code="text.account.consent.button.collapseExpand" />
	</button>
</div>
<div class="panel-body collapse in" id="consent-management-form" data-consent-management-url="${consentManagementUrl}">
	<c:if test="${not empty consentTemplateDataList}">
		<ul class="account-section-form__toggle-list clearfix">
			<c:forEach items="${consentTemplateDataList}" var="consentTemplateData">
				<li>
					<c:set var="consentTemplateId" value="${fn:escapeXml(consentTemplateData.id)}" />
					<label for="${consentTemplateId}" class="control-label">${fn:escapeXml(consentTemplateData.name)}</label>
					<label class="toggle-switch">
						<c:choose>
							<c:when test="${not empty consentTemplateData.consentData && empty consentTemplateData.consentData.consentWithdrawnDate}">
								<input class="toggle-switch__input" id="${consentTemplateId}" type="checkbox" checked />
								<span class="toggle-switch__slider toggle-switch__slider--round"></span>
							</c:when>
							<c:otherwise>
								<input class="toggle-switch__input" id="${consentTemplateId}" type="checkbox">
								<span class="toggle-switch__slider toggle-switch__slider--round"></span>
							</c:otherwise>
						</c:choose>
					</label>
					<spring:url value="/my-account/consents/give/{/consentTemplateId}/{/version}" var="giveConsentUrl" htmlEscape="false">
						<spring:param name="consentTemplateId" value="${consentTemplateData.id}" />
						<spring:param name="version" value="${consentTemplateData.version}" />
					</spring:url>
					<form:form action="${giveConsentUrl}" method="POST">
						<button hidden type="submit" id="give-consent-button-${consentTemplateId}"></button>
					</form:form>
					<spring:url value="/my-account/consents/withdraw/{/consentCode}" var="withdrawConsentUrl" htmlEscape="false">
						<spring:param name="consentCode" value="${consentTemplateData.consentData.code}" />
					</spring:url>
					<form:form action="${withdrawConsentUrl}" method="POST">
						<button hidden type="submit" id="withdraw-consent-button-${consentTemplateId}"></button>
					</form:form>
				</li>
			</c:forEach>
		</ul>
	</c:if>
</div>
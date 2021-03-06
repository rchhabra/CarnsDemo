<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="idKey" required="true" type="java.lang.String"%>
<%@ attribute name="labelKey" required="true" type="java.lang.String"%>
<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="labelCSS" required="false" type="java.lang.String"%>
<%@ attribute name="inputCSS" required="false" type="java.lang.String"%>
<%@ attribute name="items" required="true" type="java.util.Collection"%>
<%@ attribute name="disabled" required="false" type="java.lang.Boolean"%>
<%@ attribute name="itemValue" required="false" type="java.lang.String"%>
<%@ attribute name="itemLabel" required="false" type="java.lang.String"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<template:errorSpanField path="${path}">
	<spring:theme code="${idKey}" var="themeIdKey" />
	<c:set var="errorsPath">
		<form:errors path="${path}" />
	</c:set>
	<c:if test="${not empty errorsPath}">
		<div class="form_field-label ${labelCSS}">
			<label class="${labelCSS}" for="${themeIdKey}">
				<span class="skip">
					<form:errors path="${path}" />
				</span>
			</label>
		</div>
	</c:if>
	<label>
		<spring:theme code="${labelKey}" />
	</label>
	<form:checkboxes element="div" cssClass="${inputCSS}" id="${themeIdKey}" path="${path}" items="${items}" disabled="${disabled}" itemValue="${not empty itemValue ? itemValue :'code'}" itemLabel="${not empty itemLabel ? itemLabel :'name'}" />
</template:errorSpanField>

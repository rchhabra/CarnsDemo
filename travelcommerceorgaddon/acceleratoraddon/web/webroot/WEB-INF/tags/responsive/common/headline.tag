<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ attribute name="url" required="true" type="java.lang.String" %>
<%@ attribute name="labelKey" required="true" type="java.lang.String" %>
<%@ attribute name="backLabelKey" required="false" type="java.lang.String" %>
<%@ attribute name="labelArguments" required="false" type="java.lang.String" %>

<spring:htmlEscape defaultHtmlEscape="true"/>

<div class="row heading-with-button">
	<div class="col-xs-7 col-sm-8">
		<h2 class="h2"><spring:theme code="${labelKey}" arguments="${labelArguments}"/></h2>
	</div>
	<c:if test="${not empty backLabelKey}">
		<div class="col-xs-5 col-sm-4 button-inline">
			<a class="btn btn-primary btn-block" href="${url}"><spring:theme code="${backLabelKey}"/></a>
		</div>
	</c:if>
</div>

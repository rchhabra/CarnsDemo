<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ attribute name="labelKey" required="true" type="java.lang.String"%>
<%@ attribute name="count" required="false" type="java.lang.String"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="panel-heading ">
	<h3 class="title">
		<spring:theme code="${labelKey}" />
		<c:if test="${not empty count}"> (<span class="y_userCounter">${fn:escapeXml(count)}</span>)</c:if>
	</h3>
</div>
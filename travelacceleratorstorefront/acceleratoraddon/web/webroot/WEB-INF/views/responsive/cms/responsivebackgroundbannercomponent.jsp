<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<c:url value="${not empty page ? page.label : urlLink}" var="encodedUrl" />

<c:set var="backgroundStyleCss">
	background-image: url('<c:out value="${media.url}" />');
</c:set>
<a href="${encodedUrl}">
	<div ${styleAttributes} style="${fn:escapeXml(backgroundStyleCss)}">${content}</div>
</a>

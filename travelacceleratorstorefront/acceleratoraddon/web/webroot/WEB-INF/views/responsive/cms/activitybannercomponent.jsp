<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:url value="${not empty page ? page.label : urlLink}" var="encodedUrl" />

<div class="trip-tile col-xs-12 col-sm-6 col-md-4">
	<a href="${encodedUrl}">
		<img src="${media.url}" alt="${fn:escapeXml(headline)}">
		<h3 class="trip-tile-heading">${fn:escapeXml(headline)}</h3>
	</a>
</div>

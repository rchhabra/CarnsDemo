<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:url value="${not empty page ? page.label : urlLink}" var="encodedUrl" />

<img src="${media.url}" class="img-responsive" alt="Side Promo">
<div class="promotion-content">${content}</div>
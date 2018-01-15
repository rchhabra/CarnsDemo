<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="accommodationSearchParams" required="true" type="java.util.Map"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<spring:htmlEscape defaultHtmlEscape="true" />


<c:url value="/package-listing" var="packageListingUrl" />
<form id="y_packageSearchSortForm" method="GET" action="${packageListingUrl}">
    <c:forEach items="${accommodationSearchParams}" var="paramDetail">
        <input type="hidden" name="${fn:escapeXml(paramDetail.key)}" value="${fn:escapeXml(paramDetail.value)}" />
    </c:forEach>
   	<input id="y_resultsViewTypeForSortForm" type="hidden" name="resultsViewType" value="${fn:escapeXml(resultsViewType )}"/>
    <input type="hidden" name="q" value="${fn:escapeXml(packageSearchResponse.criterion.query)}" />
    <label for="y_packageSearchSortSelect">
        <spring:theme code="accommodation.sort.by.title" text="Sort by:" />
    </label>
    <select class="form-control" name="sort" id="y_packageSearchSortSelect">
        <option selected disabled>
            <spring:theme code="accommodation.sort.order.select.sort" text="Select a sort" />
        </option>
        <c:forEach var="sort" items="${packageSearchResponse.criterion.sorts}">
            <option value="${fn:escapeXml(sort.code)}" ${sort.selected ? 'selected' : ''}>
                <c:choose>
                    <c:when test="${not empty sort.name}">
                        ${fn:escapeXml(sort.name)}
                    </c:when>
                    <c:otherwise>
                        <spring:theme code="accommodation.sort.order.${sort.code}" text="${fn:escapeXml(sort.code)}" />
                    </c:otherwise>
                </c:choose>
            </option>
        </c:forEach>
    </select>
</form>

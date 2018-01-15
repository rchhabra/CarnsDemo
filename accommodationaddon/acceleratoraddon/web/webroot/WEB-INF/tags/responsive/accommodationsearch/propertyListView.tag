<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="propertiesListParams" required="true" type="java.util.List"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="accommodationsearch" tagdir="/WEB-INF/tags/addons/accommodationaddon/responsive/accommodationsearch"%>
<c:if test="${fn:length(propertiesListParams) gt 0}">
    <c:forEach var="property" items="${propertiesListParams}" varStatus="propID">
        <accommodationsearch:propertyListItem property="${property}" stayDateRange="${accommodationSearchResponse.criterion.stayDateRange}"/>
    </c:forEach>
</c:if>

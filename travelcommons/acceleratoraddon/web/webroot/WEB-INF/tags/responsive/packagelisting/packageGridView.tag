<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="propertiesListParams" required="true" type="java.util.List"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="packagelisting" tagdir="/WEB-INF/tags/addons/travelcommons/responsive/packagelisting"%>
<c:if test="${fn:length(propertiesListParams) gt 0}">
    <c:forEach var="property" items="${propertiesListParams}" varStatus="propID">
        <packagelisting:packageGridItem packageData="${property}" stayDateRange="${packageSearchResponse.criterion.stayDateRange}" />
    </c:forEach>
</c:if>
